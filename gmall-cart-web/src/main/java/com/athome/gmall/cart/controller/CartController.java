package com.athome.gmall.cart.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.athome.gmall.bean.OmsCartItem;
import com.athome.gmall.bean.PmsSkuInfo;
import com.athome.gmall.service.CartService;
import com.athome.gmall.service.SkuService;
import com.athome.gmall.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//购物车分为；两种，一种是登录状态下，一种是没有登录状态
@Controller
public class CartController {

    @Reference
    CartService cartService;
    @Reference
    SkuService skuService;

    //要返回一个重定向
    //两种方式
    //一种是ModelAndView
    //另一种使用redirect
    @RequestMapping("addToCart")
    public String addToCart(String skuId, BigDecimal quantity, HttpServletRequest request, HttpServletResponse response) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        // String ip =

        //调用商品服务，查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId, "");
        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(quantity);

        //判断用户是否登录
        String memberId = "1";
        //根据用户登录状态决定走cookie分支还是db
        //购物车数据进行写入
        if (StringUtils.isBlank(memberId)) {
            //用户没有登录,操作cookie：response.addCookie(cookie);获得cookie数据：request.getCookies();
            // 根据cookie的key拿到需要的数据
            //cookie有跨域问题，火狐有个cookie管理工具cookiemanager，在存储cookie和取得cookie数据的时候都对域进行设置
            //getDomain seDomain(设置cookie的方法)
//参数分别为：request，response，cookie的名字，值，过期时间，是否进行utf-8编码
            //DB中的购物车叫carListDb( 有主键和用户ID)
            //cookie中叫cartListCookie（没有主键和用户ID）
            //Redis中叫cartListRedis（有主键和用户ID）
            //cookie中存储的数据应该是一个集合,因为db和catch中一定是某个用户的
            //List<OmsCartItem> omsCartItems = new ArrayList<>();

            //防止cookie中已经添加了一个cookie，但是后面来的会把相同id的覆盖掉，所以先将原来的值拿出来，放到list里面，再把后面的放进去之后，再添加进cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            //判断cookie是否为空
            if (StringUtils.isBlank(cartListCookie)) {
                omsCartItems.add(omsCartItem);
            } else {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);

                //判断添加的购物车数据在cookie中是否存在
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if (exist) {
                    //之前添加过，更新购物车的添加数量

                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                            // cartItem.setPrice(cartItem.getPrice().add(omsCartItem.getPrice()));
                        }
                    }

                } else {
                    //之前没有添加过，新增当前购物车
                    omsCartItems.add(omsCartItem);
                }
            }

            //更新cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);

        } else {
            //用户已经登录
            //DB+缓存（Redis）  缓存主要用来查

            //从DB中查出购物车数据


         OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId,skuId);
            if (omsCartItemFromDb==null){
                //该用户没有添加过当前商品
                omsCartItem.setMemberId(memberId);
                omsCartItem.setMemberNickname("");
                omsCartItem.setQuantity(quantity);
                cartService.addCart(omsCartItem);
            }else {
                //改用户添加过当前商品
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);
            }
            //同步缓存
            cartService.flushCartCache(memberId);
            // List<OmsCartItem> omsCartItems = new ArrayList<>();
           // omsCartItems = cartService.getCartsByUser(memberId);

//            if (omsCartItems == null) {
//                //db为空
//
//
//            } else {
//                //db不空
//
//                //判断是否重复
//                if (true) {
//
//                } else {
//
//                }
//
//            }

        }
        return "redirect:/success.html";
    }

    @RequestMapping("cartList")
    public String cartList(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();

        String userId = "1";
        if (StringUtils.isNotEmpty(userId)){
            //已经登录 查询db
            omsCartItems = cartService.cartList(userId);
        }else{
            //没有登录  查询cookie

            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotEmpty(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }

        }
        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }

        modelMap.put("cartList",omsCartItems);
        return "cartList";
    }
    @RequestMapping("checkCart")
    public String checkCart(String isChecked, String skuId, HttpServletResponse response, HttpServletRequest request, HttpSession session,ModelMap modelMap){
        String memberId = "1";

        //调用服务，修改状态

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        cartService.checkCart(omsCartItem);
        //将最新的数据从缓存中查出，渲染给内嵌页

        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
        modelMap.put("cartList",omsCartItems);

        return "cartListInnner";
    }
    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean b = false;
        for (OmsCartItem cartItem : omsCartItems) {
            String productId = cartItem.getProductId();
            if (productId.equals(omsCartItem.getProductSkuId())) {
                b = true;
            }
        }
        return b;
    }
//    @RequestMapping("addCart")
//    public String addCart(){
//
//        return "success";
//    }
}
