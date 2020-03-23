package com.athome.gmall.cart.controller;

import java.math.BigDecimal;

public class TestBigDecimal {


    public static void main(String[] args) {
        //初始化
        //8个初始化方法
        //java中小数是用分数的幂函数
        BigDecimal b1 = new BigDecimal(0.01f);
        BigDecimal b2 = new BigDecimal(0.01d);
        BigDecimal b3 = new BigDecimal("0.01");


        BigDecimal b4 = new BigDecimal("6");
        BigDecimal b5= new BigDecimal("7");

        //比较

        int i = b1.compareTo(b2);//返回结果有三个值：1代表大于  0代表等于  -1代表小于

        //运算

        //加
        BigDecimal add = b1.add(b2);
        //减
        BigDecimal subtract = b2.subtract(b1);
        //乘
        BigDecimal multiply = b4.multiply(b5);
        //除
        BigDecimal divide = b4.divide(b5,BigDecimal.ROUND_HALF_DOWN);//四舍五入


        //约数

        BigDecimal subtract1 = b2.subtract(b1);
        subtract1.setScale(3,BigDecimal.ROUND_HALF_DOWN);//第一个参数是保留位数，第二个是四舍五入
    }
}
