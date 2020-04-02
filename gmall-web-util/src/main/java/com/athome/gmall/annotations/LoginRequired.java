package com.athome.gmall.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//定义使用范围、生效范围  通过元注解来定义
//@Retention：注解的保留位置　　　　　　　　　
//
//       @Retention(RetentionPolicy.SOURCE)   //注解仅存在于源码中，在class字节码文件中不包含
//
//　　　　@Retention(RetentionPolicy.CLASS)     // 默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得，
//
//　　　　@Retention(RetentionPolicy.RUNTIME)  // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
//
//　　
//
//@Target:注解的作用目标　　　　　　　　
//
//       @Target(ElementType.TYPE)   //接口、类、枚举、注解
//
//　　　　@Target(ElementType.FIELD) //字段、枚举的常量
//
//　　　　@Target(ElementType.METHOD) //方法
//
//　　　　@Target(ElementType.PARAMETER) //方法参数
//
//　　　　@Target(ElementType.CONSTRUCTOR)  //构造函数
//
//　　　　@Target(ElementType.LOCAL_VARIABLE)//局部变量
//
//　　　　@Target(ElementType.ANNOTATION_TYPE)//注解
//
//　　　　@Target(ElementType.PACKAGE) ///包
//
//
//
//@Document：说明该注解将被包含在javadoc中
//
//       @Inherited：说明子类可以继承父类中的该注解


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
    //登录校验的注解，定义注解要用@interface修饰
    //因为购物车是无论登录与否  都可以通过验证，但是后面的逻辑不同，所以要加一个判断
    boolean loginSuccess() default true;
}
