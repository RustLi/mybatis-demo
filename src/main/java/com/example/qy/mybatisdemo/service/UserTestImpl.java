package com.example.qy.mybatisdemo.service;

import com.example.qy.mybatisdemo.dal.user.entity.User;
import com.example.qy.mybatisdemo.dal.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTestImpl implements UserTest{

    @Autowired
    private UserMapper userMapper;

    @Override
    public void getUser() {
        System.out.println("222");
        User user = userMapper.selectByPrimaryKey(1);
        System.out.println("222 user : " + user);

    }
}
