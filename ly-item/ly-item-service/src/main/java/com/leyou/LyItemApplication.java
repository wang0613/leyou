package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;


//注意启动类的包扫描，确保所有的类都可以扫描到
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.item.mapper") //通用mapper的包扫描
public class LyItemApplication {
    public static void main(String[] args) {

        SpringApplication.run(LyItemApplication.class,args);
    }
}
