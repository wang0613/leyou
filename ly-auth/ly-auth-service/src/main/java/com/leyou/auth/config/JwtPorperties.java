package com.leyou.auth.config;

import com.leyou.common.utils.RsaUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: jwt属性类
 * @date 2021/2/25 15:25
 */
@ConfigurationProperties(prefix = "ly.jwt")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class JwtPorperties {

    private String secret;     //登录校验的密钥 salt值
    private String pubKeyPath; // 公钥地址
    private String priKeyPath; //  私钥地址
    private int expire;   //过期时间,单位分钟
    private String cookieName;   //cookie名称


    //保存初始化后生成额的 公钥和私钥
    private PublicKey publicKey; // 公钥
    private PrivateKey privateKey; // 私钥

    /**
     * @PostContruct： 在构造方法执行之后执行该方法
     */
    @PostConstruct
    public void init(){
        try {
            File pubKey = new File(pubKeyPath);
            File priKey = new File(priKeyPath);
            //如果没有公钥和私钥生成
            if (!pubKey.exists() || !priKey.exists()) {
                // 生成公钥和私钥
                RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }
}
