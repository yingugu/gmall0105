package com.athome.gmall.user.controller;

import com.athome.gmall.user.bean.UmsMember;
import com.athome.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @RequestMapping("index")
    public String index(){
        return "hello user";
    }
    @RequestMapping("getAllUser")
    /**
    * @getAllUser
     * @author 将所有的ums_menmber中的东西搜索出来
    *
    */
    public List<UmsMember> getAllUser(){

        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }
}
