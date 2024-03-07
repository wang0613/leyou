package com.leyou.order.config;

import com.leyou.common.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 将IdWorker注册到spring容器中
 * @date 2021/3/4 9:59
 */
@EnableConfigurationProperties(IdWorkerProperties.class)
@Configuration
public class IdWorkerConfig {

    //使用构造函数 参数的方式将 配置文件准入

    /**
     * 使用构造函数将idWorker注册到容器中
     * @param prop 所需参数
     * @return IdWorker
     */
    @Bean
    public IdWorker idWorker(IdWorkerProperties prop){
        return new IdWorker(prop.getWorkerId(),prop.getDatacenterId());
    }
}
