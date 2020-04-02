package com.athome.gmall.passport.controller;/**
 * @version 0.1.0
 * @Description
 * @author yingugu
 * @create 2020-04-01 15:15
 * @since 0.1.0
 **/

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.athome.gmall.bean.UmsMember;
import com.athome.gmall.service.UserService;
import com.athome.gmall.util.HttpclientUtil;
import com.athome.gmall.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <h3>gmall0105</h3>
 * <p>认证中心类</p>
 * @author : 尹平
 * @date : 2020-04-01 15:15
 **/
@Controller
public class PassportController {

    @Reference
    UserService userService;


    /**
     *
     * @param code
     * @param request
     * @return String
     * @/**
     * @Description: 社交登录验证
     * @version 0.1.0
     * @return
     * @author yingugu
     * @date 2020/4/1 17:00
     */

    @RequestMapping("vlogin")
    public String vlogin(String code,HttpServletRequest request){
        //授权码换区access_token
        String s3 = values.uri;   // ?client_id=657937913&client_secret=248453816ca35f13c543c9fb760a8200&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";

//        //创建httpclient对象
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        //创建post请求
//        HttpPost httpPost = new HttpPost(s3);

        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id",values.client_id);
        paramMap.put("client_secret",values.client_secret);
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri",values.redirect_uri);
        //要注意这里的code  会很快过期
        paramMap.put("code",code);//授权码有效期内可以用，每重新生成一次授权码，代表着一次重新授权。
        String access_token = HttpclientUtil.doPost(s3, paramMap);
        Map<String,Object> map = JSON.parseObject(access_token, Map.class);

        String accessToken = (String) map.get("access_token");
        String uid =(String) map.get("uid");
        //access_token换取用户信息

        String show_user_url = "https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid;

        String user_json = HttpclientUtil.doGet(show_user_url);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);
        //将用户信息存进数据库，用户类型设置为微博用户



        UmsMember umsMember = new UmsMember();
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(accessToken);
        umsMember.setSourceType("2");
        umsMember.setSourceUid((String) user_map.get("idstr"));
        umsMember.setCity((String)user_map.get("location"));
        umsMember.setNickname((String)user_map.get("screen_name"));
        String gender = (String)user_map.get("gender");
        String g ="0";

        if (gender.equals("m")){

            g = "1";
        }
        umsMember.setGender(g);

        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        UmsMember umsMemberCheck = userService.checkOauthUser(umsCheck);

        if(umsMemberCheck==null){
            userService.addOauthUser(umsMember);
        }else{
            umsMember = umsMemberCheck;
        }

        // 生成jwt的token，并且重定向到首页，携带该token
        String token = null;
        String memberId = umsMember.getId();
        String nickname = umsMember.getNickname();
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("memberId",memberId);
        userMap.put("nickname",nickname);


        String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();// 从request中获取ip
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }

        // 按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("2020gmall0105", userMap, ip);

        // 将token存入redis一份
        userService.addUserToken(token,memberId);


        return "redirect:http://search.gmall.com:8083/index?token="+token;
    }


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
