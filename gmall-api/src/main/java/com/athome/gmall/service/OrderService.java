package com.athome.gmall.service;/**
 * @package_name com.athome.gmall.service
 * @version 0.1.0
 * @Description
 * @author yinping
 * @create 2020-04-03 16:04
 * @since 0.1.0
 **/

/**
 * @program: gmall0105
 *
 * @description:订单提交service接口
 *
 * @author: yinping
 *
 * @create: 2020-04-03 16:04
 **/
public interface OrderService {
    String checkTradeCode(String memberId,String tradeCode);

    String genTradeCode(String memberId);
}
