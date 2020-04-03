package com.athome.gmall.order.controller;/**
 * @package_name com.athome.gmall.order.controller
 * @version 0.1.0
 * @Description
 * @author yinping
 * @create 2020-04-02 15:46
 * @since 0.1.0
 **/

import com.alibaba.dubbo.config.annotation.Reference;
import com.athome.gmall.annotations.LoginRequired;
import com.athome.gmall.bean.OmsCartItem;
import com.athome.gmall.bean.OmsOrder;
import com.athome.gmall.bean.OmsOrderItem;
import com.athome.gmall.bean.UmsMemberReceiveAddress;
import com.athome.gmall.service.CartService;
import com.athome.gmall.service.OrderService;
import com.athome.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>gmall0105</h3>
 * <p>结算页面controller</p>
 * @author : yinping
 * @date : 2020-04-02 15:46
 **/
@Controller
public class OrderController {



    @Reference
    CartService cartService;
    @Reference
    UserService userService;
    @Reference
    OrderService orderService;


    @RequestMapping("submitOrder")
    @LoginRequired(loginSuccess = true)
    public String submitOrder(String tradeCode,String receiveAddressId,BigDecimal totalAmount, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){


        String memberId = (String) request.getAttribute("memberId");//这里不能用toString方法，防止null.toString报空指针异常
        String nikeName = (String) request.getAttribute("nikeName");
        //检查交易码

        String success = orderService.checkTradeCode(memberId,tradeCode);
        if (success.equals("success")){

            //根据用户ID获得要购买的商品列表（购物车中取），和总价格
            //页面中唯一需要选择的只有地址


            //提交之后，将订单和订单详情写入数据库
            //删除购物车中的对应商品
            //需要防止用户重复提交同一个订单(交易码，每次提交的时候，都要生成一个交易码，每个交易码都不能重复使用)



            //重定向到支付系统
        }else{
            return "fail";
        }



        return null;
    }

    /**
     *
     * @param response
     * @param request
     * @param modelMap
     * @Description: 订单详情
     * @version 0.1.0
     * @author yingugu
     * @date 2020/4/2 17:17
     * @return
     */
    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = true)
    public String toTrade (HttpServletResponse response, HttpServletRequest request, ModelMap modelMap){

        String memberId = (String) request.getAttribute("memberId");//这里不能用toString方法，防止null.toString报空指针异常
        String nikeName = (String) request.getAttribute("nikeName");


        //用户收货地址列表

        List<UmsMemberReceiveAddress> receiveAddressByMemberId = userService.getReceiveAddressByMemberId(memberId);


        //将购物车集合转化为页面计算清单集合
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);


        List<OmsOrderItem> omsOrderItems = new ArrayList<>();

        for (OmsCartItem omsCartItem : omsCartItems) {
            //选中状态才能提取数据进行封装
            if (omsCartItem.getIsChecked().equals("1")){
                //每循环一个购物车对象，就封装一个商品的详情到OmsOrderItems
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductName(omsCartItem.getProductName());

                omsOrderItem.setProductQuantity(omsCartItem.getQuantity().intValue());
                omsOrderItems.add(omsOrderItem);
            }
        }


        modelMap.put("omsOrderItems",omsOrderItems);
        modelMap.put("userAddressList", receiveAddressByMemberId);
        modelMap.put("totalAmount", getTotalAmount(omsCartItems));

        //生成交易码，为了在提交订单时做交易码校验
       String tradeCode = orderService.genTradeCode(memberId);
        modelMap.put("tradeCode",tradeCode);

        return "trade";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();

            if (omsCartItem.getIsChecked().equals("1")) {
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }


}
