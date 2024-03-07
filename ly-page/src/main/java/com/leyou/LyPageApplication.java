package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: page微服务启动类
 * @date 2021/2/11 16:18
 */
@SpringBootApplication
@EnableDiscoveryClient //eureka客户端
@EnableFeignClients //feign的远程调用
public class LyPageApplication {

    public static void main(String[] args) {

        SpringApplication.run(LyPageApplication.class);
    }
}
