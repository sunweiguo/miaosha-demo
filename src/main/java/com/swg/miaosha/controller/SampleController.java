package com.swg.miaosha.controller;

import com.swg.miaosha.model.User;
import com.swg.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @Autowired
    private UserService userService;

    @RequestMapping("user")
    public User getUser(){
        return userService.getUser();
    }

    @RequestMapping("insert")
    @Transactional
    public void insertUser(){
        User user1 = new User();
        user1.setId(2);
        user1.setName("hh");

        userService.insertUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setName("daguozi");
        userService.insertUser(user2);
    }
}
