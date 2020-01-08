package com.athome.gmall.user.service.impl;

import com.athome.gmall.user.mapper.UserMapper;
import com.athome.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
   // public UserMapper
}
