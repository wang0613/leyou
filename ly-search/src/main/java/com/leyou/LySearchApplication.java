package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @description: 搜索启动类
 * @author: haiLong_wang
 * @time: 2021/1/30 21:15
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  //开启feign
public class LySearchApplication {
    public static void main(String[] args) {

        SpringApplication.run(LySearchApplication.class,args);
    }
}
