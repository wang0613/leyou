package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 配置类
 * @date 2021/3/4 9:57
 */
@Data
@ConfigurationProperties(prefix = "ly.worker")
public class IdWorkerProperties {


    //机器id
    private long workerId;
    //序列号
    private long datacenterId;
}
