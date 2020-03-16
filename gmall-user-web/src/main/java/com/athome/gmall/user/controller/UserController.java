package com.athome.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.bean.UmsMember;
import com.athome.gmall.bean.UmsMemberReceiveAddress;
import com.athome.gmall.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
//    @Autowired
//    UserService userService;
    @Reference
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

    @RequestMapping("getReceiveAddressByMemberId")
    public List<UmsMemberReceiveAddress> getReciveAddressByMemberId( String memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = userService.getReceiveAddressByMemberId(memberId);
        return umsMemberReceiveAddresses;

    }
}
