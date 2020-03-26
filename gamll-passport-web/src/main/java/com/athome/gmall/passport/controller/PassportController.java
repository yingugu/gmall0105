package com.athome.gmall.passport.controller;

import com.athome.gmall.bean.UmsMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PassportController {

    @RequestMapping("verify")
    public String verify(String token){

        //通过jwt校验真假
        return  "success";
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap modelMap){
        modelMap.put("ReturnUrl",ReturnUrl);
    return "index";
    }
    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember){


        //调用用户服务，验证用户名和密码

        //
        return null;
    }
}
