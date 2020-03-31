package com.athome.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.athome.gmall.util.HttpclientUtil;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.HashMap;
import java.util.Map;

public class TestOauth2 {

    public static String getCode(){
        //client id  657937913
        //授权回调地址  http://passport.gmall.com:8085/vlogin
        String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=657937913&response_type=code&redirect_uri=http://passport.gmall.com:8085/vlogin");

        //第一步和第二部返回回调地址之间，有一个用户操作授权的过程；
        String s2 = "http://passport.gmall.com:8085/vlogin?code=15f2a4efa75e649316a292537451fb90";
        return s1;
    }
    public Map<String,String> getAccess_token(){


        String s3 = "https://api.weibo.com/oauth2/access_token";   // ?client_id=657937913&client_secret=248453816ca35f13c543c9fb760a8200&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";

//        //创建httpclient对象
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        //创建post请求
//        HttpPost httpPost = new HttpPost(s3);

        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","657937913");
        paramMap.put("client_secret","248453816ca35f13c543c9fb760a8200");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        //要注意这里的code  会很快过期
        paramMap.put("code","0b957175f2abcfb9e52f738057e9cc7e");

//为了防止重新授权，将此处注掉
        String access_token = HttpclientUtil.doPost(s3, paramMap);

        Map<String,String> map = JSON.parseObject(access_token, Map.class);
        String accessToken = map.get("access_token");
        String uid = map.get("uid");

        System.out.println(accessToken);
        return map;
    }
    public Map<String,String> getUser_info( Map<String,String>  map){

        //用access_token获取用户信息
        //user_id (需要查询的用户ID)和screen_name（需要查询的用户昵称）二者必选其一且只能选其一
        String s4 = "https://api.weibo.com/2/users/show.json?access_token="+map.get("access_token")+"&uid="+map.get("uid")+"";
        String s = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(s, Map.class);
        System.out.println(s);
        return user_map;
    }

    public static void main(String[] args) {


        getCode();




    }
}
