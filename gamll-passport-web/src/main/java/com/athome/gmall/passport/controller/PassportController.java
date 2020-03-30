package com.athome.gmall.passport.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.athome.gmall.bean.UmsMember;
import com.athome.gmall.service.UserService;
import com.athome.gmall.util.JwtUtil;
import com.athome.gmall.util.MD5Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    UserService userService;

    @RequestMapping("verify")
    public String verify(String token, String currentIp) {

        //通过jwt校验真假
        Map<String, String> map = new HashMap<>();
//只有verify认证中心有权利调这个jwtutil
        Map<String, Object> decode = JwtUtil.decode(token, "2020gmall0105", currentIp);
        if (decode != null) {
            map.put("status", "success");
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nikeName", (String) decode.get("nikeName"));
        } else {
            map.put("status", "fail");
        }

        return JSON.toJSONString(map);
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap modelMap) {
        modelMap.put("ReturnUrl", ReturnUrl);
        return "index";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {


        String token = "";

        //调用用户服务，验证用户名和密码

        UmsMember umsMembeLogin = userService.login(umsMember);
        if (umsMembeLogin != null) {

            //登陆成功

            //用jwt制作token
            String memberId = umsMembeLogin.getId();
            String nikName = umsMembeLogin.getNickname();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("memberId", memberId);
            userMap.put("nikeName", nikName);
            //从request中获取IP,如果请求直接来自客户机的话，此时用这个String remotAddr = request.getRemoteAddr();
            //如果中间有nginx，获取的就是nginx的IP，此时要用 request.getHeader(),
            // 再在nginx中做一个标签x-forwarded-for,代表nginx转发的那个请求地址
            String ip = request.getHeader("x-forwarded-for");//通过ngixn转发的客户ip
            if (StringUtils.isBlank(ip)) {

                ip = request.getRemoteAddr();
            }

            if (StringUtils.isBlank(ip)) {
                //如果都为空，就是出问题了,正常要进行异常处理，这里暂时直接写死
                ip = "127.0.0.1";
            }
            //转码的时候，盐值尽量进行MD5加密，否则加密出来的结果是一样的
            //算法加密之后的token
            token = JwtUtil.encode("2020gmall0105", userMap, ip);

            //将token存入Redis一份
            userService.addUserToken(token, memberId);

        } else {
            //登陆失败
            token = "fail";

        }

        //
        return token;
    }
}
