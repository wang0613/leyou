package com.leyou.cart.config;

import com.leyou.common.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/3/1 20:00
 */
@ConfigurationProperties(prefix = "ly.jwt")
@Data
@Slf4j
public class JwtProperties {

    private String pubKeyPath; //公钥地址
    private String cookieName;  // cookie名称

    private PublicKey publicKey; // 公钥


    //读取公钥
    @PostConstruct
    public void init(){
        try {
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥失败！", e);
            throw new RuntimeException();
        }
    }
}
