package com.athome.gmall.util;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestJwt {
//token生成测试
    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("memberId","1");
        map.put("nikeName","yinping");
        String ip = "172.16." +
                "40.92";
        String time = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
        String encode = JwtUtil.encode("2020gmall0105", map, ip + time);
        System.err.println(encode);



        // BASE64
//        String tokenUserInfo = StringUtils.substringBetween(encode, ".");
//        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
//        byte[] tokenBytes = base64UrlCodec.decode(tokenUserInfo);
//        String tokenJson = null;
//        try {
//            tokenJson = new String(tokenBytes, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Map map =  JSON.parseObject(tokenJson, Map.class);
//        System.out.println("64="+map);



    }
}
