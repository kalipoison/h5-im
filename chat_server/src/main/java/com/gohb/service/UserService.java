package com.gohb.service;




import com.gohb.netty.ChatMsg;
import com.gohb.pojo.FriendsRequest;
import com.gohb.pojo.User;
import com.gohb.vo.FriendsRequestVO;
import com.gohb.vo.MyFriendsVO;

import java.util.List;

public interface UserService {

    User getUserById(String id);

    //根据用户名查找指定用户对象
    User queryUserNameIsExit(String username);

    //保存
    User insert(User user);

    //修改用户
    User updateUserInfo(User user);

    //搜索好友的前置条件接口
    Integer preconditionSearchFriends(String myUserId, String friendUserName);

    //发送好友请求
    void  sendFriendRequest(String myUserId,String friendUserName);

    //好友请求列表查询
    List<FriendsRequestVO> queryFriendRequestList(String acceptUserId);

    //处理好友请求——忽略好友请求
    void deleteFriendRequest(FriendsRequest friendsRequest);

    //处理好友请求——通过好友请求
    void passFriendRequest(String sendUserId,String acceptUserId);

    //好友列表查询
    List<MyFriendsVO> queryMyFriends(String userId);

    //保存用户聊天消息
    String saveMsg(ChatMsg chatMsg);

    void updateMsgSigned(List<String> msgIdList);

    //获取未签收的消息列表
    List<com.gohb.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);
}
