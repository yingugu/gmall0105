package com.athome.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.athome.gmall.manage.mapper")
//这个springboot启动类，为了能扫描到Redis的两个配置类，所以向上提了一个包，因为扫描的时候，只能向下扫描，不能平级或者向上扫描，也可以用componentscan，但是componentscam只能用一次，
// 所以不到关键时刻不用
public class  GmallManageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallManageServiceApplication.class, args);
    }

}
