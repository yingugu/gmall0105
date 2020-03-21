package com.athomegmall.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//购物车分为；两种，一种是登录状态下，一种是没有登录状态
@Controller
public class CartController {
    //要返回一个重定向
    //两种方式
    //一种是ModelAndView
    //另一种使用redirect
    @RequestMapping("addToCart")
    public String addToCart(){

        return "redirect:/success.html";
    }
//    @RequestMapping("addCart")
//    public String addCart(){
//
//        return "success";
//    }
}
