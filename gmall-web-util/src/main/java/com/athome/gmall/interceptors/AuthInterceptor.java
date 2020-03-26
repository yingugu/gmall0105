package com.athome.gmall.interceptors;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.athome.gmall.annotations.LoginRequired;
import com.athome.gmall.util.CookieUtil;
import com.athome.gmall.util.HttpclientUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //拦截代码
        //1、判断被拦截的请求的访问的方法的注解，判断是否是需要拦截的
        //每个httprequest都带着方法名
        //通过方法名获得方法的具体信息
        HandlerMethod hm = (HandlerMethod) handler;
        //获得注解信息
        //通过反射的方法，反射是通过类对象或类名获得类的整体信息的过程
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);

        //判断是否进行拦截
        String token = "";
        //cookie中的token
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotEmpty(oldToken)) {
            token = oldToken;
        }
        //地址栏中的token
        String newToken = request.getParameter("token");
        if (StringUtils.isNotEmpty(newToken)) {
            token = newToken;
        }
        if (methodAnnotation == null) {
            return true;
        } else {
            //进入拦截器的拦截方法
            //方法应该分为三种，第一类是不需要拦截的（没有加注解）

            //第二类是需要拦截，但是校验失败也可以继续访问的方法（没有登录或者登录过期了，比如购物车中的方法）loginRequired(false)
            //是否必须登录
            boolean loginSuccess = methodAnnotation.loginSuccess();//获得该请求是否必须登录成功

            //调用认证中心进行验证 （远程调用，因为社交模块后面可能要被其他系统调用，所以一定要写成rest风格的http请求）,
            // token不为空的时候才有机会验证
            String success = "fail";
            if (StringUtils.isNotEmpty(token)){

            HttpclientUtil.doGet("http://passport.gmall.com:8085/verify?token" + token);
            }

            if (loginSuccess) {
                //必须登录成功才能使用

                if (!success.equals("success")) {
                    //从定向回passport登录

                    //通过request得到当前请求
                    StringBuffer requestURL = request.getRequestURL();//两个方法，URI是不带http的   URL是全的
                    response.sendRedirect("http://passport.gamll.com:8085/index?ReturnUrl="+requestURL);
                    return false;
                } else {
                    //将token携带的用户信息写入
                    request.setAttribute("memberId", "");
                    request.setAttribute("nikName", "");
                    //验证通过，覆盖cookie中的token（为了更新过期时间），因为两个else中都需要写入cookie，所以将方法提出来放下面

                }
                //因为下面的两个else中都需要验证，所以将验证部分提取到这里


            } else {
                //校验失败也能通过，但是必须验证，因为影响购物车分支
                if (success.equals("success")) {
                    //如果不登录也能使用且验证通过了，需要将token携带的用户信息写入
                    request.setAttribute("memberId", "");
                    request.setAttribute("nikName", "");
                }
            }
            //第三类是需要拦截，且拦截一定要通过（用户登录成功了才能访问）loginRequired(true)
        }

        //更新cookie
        if (StringUtils.isNotEmpty(token)){

        CookieUtil.setCookie(request,response,"oldToken",token,2*60*60,true);
        }

        return true;
    }
}
