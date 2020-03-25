package com.athome.gmall.interceptors;

import com.athome.gmall.annotations.LoginRequired;
import com.athome.gmall.util.CookieUtil;
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


        if (methodAnnotation == null){
            return true;
        }else{
            //进入拦截器的拦截方法
            //方法应该分为三种，第一类是不需要拦截的（没有加注解）

            //第二类是需要拦截，但是校验失败也可以继续访问的方法（没有登录或者登录过期了，比如购物车中的方法）loginRequired(false)
            boolean b = methodAnnotation.loginSuccess();//获得该请求是否必须登录成功
            //第三类是需要拦截，且拦截一定要通过（用户登录成功了才能访问）loginRequired(true)



        }

        String method = request.getMethod();

        return true;
    }
}
