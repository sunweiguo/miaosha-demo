package com.swg.miaosha.service;

import com.swg.miaosha.dao.mapper.UserMapper;
import com.swg.miaosha.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUser(){
        return userMapper.getUser();
    }

    public void insertUser(User user){
        userMapper.insertUser(user.getId(),user.getName());
    }
}
