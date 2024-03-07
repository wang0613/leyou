package com.leyou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 拦截白名单
 * @date 2021/2/28 15:15
 */
@Data
@ConfigurationProperties(prefix = "ly.filter")
public class FilterProperties {

     private List<String> allowPaths;




}
