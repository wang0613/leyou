package com.leyou.order.config;

import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

/**
 * 实现微信支付的接口，注册商户号等相关信息
 * @author HAILONG_WANG
 */
@Configuration
@EnableConfigurationProperties(PayProperties.class)
public class PayConfig implements WXPayConfig {


    @Autowired
    private PayProperties payProperties;

    /**
     * 公众账号ID
     * @return String
     */
    @Override
    public String getAppID() {
        return payProperties.getAppId();
    }

    /**
     * 获取商户号ID
     * @return string
     */
    @Override
    public String getMchID() {
        return payProperties.getMchId();
    }

    /**
     * 微信支付生成签名的秘钥
     * @return string
     */
    @Override
    public String getKey() {
        return payProperties.getKey();
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    /**
     * 连接超时时长
     * @return int
     */
    @Override
    public int getHttpConnectTimeoutMs() {
        return payProperties.getConnectTimeoutMs();
    }

    /**
     * 读取超时时长
     * @return int
     */
    @Override
    public int getHttpReadTimeoutMs() {
        return payProperties.getReadTimeoutMs();
    }

}
