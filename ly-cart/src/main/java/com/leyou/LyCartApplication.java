package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 购物车微服务启动类
 * @date 2021/3/1 19:45
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LyCartApplication {
    public static void main(String[] args) {

        SpringApplication.run(LyCartApplication.class,args);
    }
}
