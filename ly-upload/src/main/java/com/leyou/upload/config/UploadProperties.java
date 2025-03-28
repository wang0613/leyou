package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 定义属性类，属性赋值
 */
@Data
@ConfigurationProperties(prefix = "ly.upload")
public class UploadProperties {

    private String baseUrl;
    private List<String> allowTypes;
}
