package com.leyou.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 属性集
 * @date 2021/2/21 21:23
 */
@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {

    private String accessKeyId;
    private String accessKeySecret;
    private String singName;
    private String verifyCodeTemplate;

}
