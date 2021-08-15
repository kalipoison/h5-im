package com.gohb.controller;

import com.gohb.pojo.User;
import com.gohb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "test";
    }

    @Autowired
    private UserService userService;

    @RequestMapping("/user")
    public void user(String id){
        User user = userService.getUserById(id);
        if(user!=null){
            System.out.println(user.toString());
            System.out.println(user.getPassword());
            System.out.println(user.getNickname());
        }

        System.out.println("/user/user");
    }



}
