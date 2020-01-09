package com.athome.gmall.user.service.impl;

import com.athome.gmall.user.bean.UmsMember;
import com.athome.gmall.user.mapper.UserMapper;
import com.athome.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<UmsMember> getAllUser() {
       List<UmsMember> umsMemberList = userMapper.selectAll();
        return umsMemberList;
    }
    // public UserMapper
}
