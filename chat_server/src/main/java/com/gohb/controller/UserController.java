package com.gohb.controller;

import com.gohb.bo.UserBO;
import com.gohb.enums.OperatorFriendRequestTypeEnum;
import com.gohb.enums.SearchFriendsStatusEnum;
import com.gohb.pojo.ChatMsg;
import com.gohb.pojo.FriendsRequest;
import com.gohb.pojo.User;
import com.gohb.service.UserService;
import com.gohb.utils.FastDFSClient;
import com.gohb.utils.FileUtils;
import com.gohb.utils.JSONResult;
import com.gohb.utils.MD5Utils;
import com.gohb.vo.FriendsRequestVO;
import com.gohb.vo.MyFriendsVO;
import com.gohb.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @RequestMapping("/registerOrLogin")
//    @ResponseBody
    //用户登录与注册一体化方法
    public JSONResult registerOrLogin(User user){
        System.out.println("User Controller registerOrLogin");
        User userResult = userService.queryUserNameIsExit(user.getUsername());
        if(userResult!=null){ //此用户存在，可登录
            if(!userResult.getPassword().equals(MD5Utils.getPwd(user.getPassword()))){
                return JSONResult.errorException("密码不正确");
            }
        }else{
            user.setNickname("wangwen");
            user.setQrcode("");
            user.setPassword(MD5Utils.getPwd(user.getPassword()));
            user.setFaceImage("");
            user.setFaceImageBig("");
            userResult = userService.insert(user);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userResult,userVo);
        System.out.println("success");
        return JSONResult.ok(userVo);
    }

    // 修改昵称方法
    @RequestMapping("/setNickname")
//    @ResponseBody
    public JSONResult setNichName(User user){
        User userResult = userService.updateUserInfo(user);
        return JSONResult.ok(userResult);
    }

    @RequestMapping("/uploadFaceBase64")
    @ResponseBody
    //用户头像上传访问方法
    public JSONResult uploadFaceBase64(@RequestBody UserBO userBO) throws Exception {
        //获取前端传过来的base64的字符串，然后转为文件对象在进行上传
        String base64Data = userBO.getFaceData();
        String userFacePath = "/usr/local/face/"+userBO.getUserId()+"userFaceBase64.png";
        //调用FileUtils 类中的方法将base64 字符串转为文件对象
        FileUtils.base64ToFile(userFacePath, base64Data);
        MultipartFile multipartFile = FileUtils.fileToMultipart(userFacePath);
        //获取fastDFS上传图片的路径
        String url = fastDFSClient.uploadBase64(multipartFile);
        System.out.println(url);
        String thump = "_150x150.";
        String[] arr = url.split("\\.");
        String thumpImgUrl = arr[0]+thump+arr[1];
//        String bigFace = "dssdklsdjsdj3498458.png";
//        String thumpFace = "dssdklsdjsdj3498458_150x150.png";
        //更新用户头像
        User user = new User();
        user.setId(userBO.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(url);
        User result = userService.updateUserInfo(user);
        return  JSONResult.ok(result);
    }


    //搜索好友的请求方法
    @RequestMapping("/searchFriend")
    @ResponseBody
    public JSONResult searchFriend(String myUserId,String friendUserName){
        /**
         * 前置条件：
         * 1.搜索的用户如果不存在，则返回【无此用户】
         * 2.搜索的账号如果是你自己，则返回【不能添加自己】
         * 3.搜索的朋友已经是你好友，返回【该用户已经是你的好友】
         */
        Integer status = userService.preconditionSearchFriends(myUserId,friendUserName);
        if(status== SearchFriendsStatusEnum.SUCCESS.status){
            User user = userService.queryUserNameIsExit(friendUserName);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            return JSONResult.ok(userVo);
        }else{
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(msg);
        }
    }

    //发送添加好友请求的方法
    @RequestMapping("/addFriendRequest")
    @ResponseBody
    public JSONResult addFriendRequest(String myUserId,String friendUserName){
        if(StringUtils.isBlank(myUserId)|| StringUtils.isBlank(friendUserName)){
            return JSONResult.errorMsg("好友信息为空");
        }

        /**
         * 前置条件：
         * 1.搜索的用户如果不存在，则返回【无此用户】
         * 2.搜索的账号如果是你自己，则返回【不能添加自己】
         * 3.搜索的朋友已经是你好友，返回【该用户已经是你的好友】
         */
        Integer status = userService.preconditionSearchFriends(myUserId,friendUserName);
        if(status==SearchFriendsStatusEnum.SUCCESS.status){
            userService.sendFriendRequest(myUserId,friendUserName);
        }else{
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(msg);
        }
        return JSONResult.ok();
    }

    //好友请求列表查询
    @RequestMapping("/queryFriendRequest")
    @ResponseBody
    public JSONResult queryFriendRequest(String userId){
        List<FriendsRequestVO> friendRequestList = userService.queryFriendRequestList(userId);
        return JSONResult.ok(friendRequestList);
    }

    //好友请求处理映射my_friends
    @RequestMapping("/operFriendRequest")
    @ResponseBody
    public JSONResult operFriendRequest(String acceptUserId,String sendUserId,Integer operType){
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setAcceptUserId(acceptUserId);
        friendsRequest.setSendUserId(sendUserId);
        if(operType== OperatorFriendRequestTypeEnum.IGNORE.type){
            //满足此条件将需要对好友请求表中的数据进行删除操作
            userService.deleteFriendRequest(friendsRequest);
        }else if(operType==OperatorFriendRequestTypeEnum.PASS.type){
            //满足此条件表示需要向好友表中添加一条记录，同时删除好友请求表中对应的记录
            userService.passFriendRequest(sendUserId,acceptUserId);
        }
        //查询好友表中的列表数据
        List<MyFriendsVO> myFriends = userService.queryMyFriends(acceptUserId);
        return JSONResult.ok(myFriends);
    }

    /**
     * 好友列表查询
     * @param userId
     * @return
     */
    @RequestMapping("/myFriends")
    @ResponseBody
    public JSONResult myFriends(String userId){
        if (StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("用户id为空");
        }
        //数据库查询好友列表
        List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);
        return JSONResult.ok(myFriends);
    }

    /**
     * 用户手机端获取未签收的消息列表
     * @param acceptUserId
     * @return
     */
    @RequestMapping("/getUnReadMsgList")
    @ResponseBody
    public JSONResult getUnReadMsgList(String acceptUserId){
        if(StringUtils.isBlank(acceptUserId)){
            return JSONResult.errorMsg("接收者ID不能为空");
        }
        //根据接收ID查找为签收的消息列表
        List<ChatMsg> unReadMsgList = userService.getUnReadMsgList(acceptUserId);
        return JSONResult.ok(unReadMsgList);

    }

}
