package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 用户微服务启动类
 * @date 2021/2/22 14:12
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.user.mapper") //通用mapper的包扫描
public class LyUserApplication {


    public static void main(String[] args) {

        SpringApplication.run(LyUserApplication.class, args);
    }
}
