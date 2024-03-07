package com.leyou.auth.test;

import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.common.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 测试生成公钥和私钥
 */
public class JwtTest {

    private static final String pubKeyPath = "C:\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "C:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 生成公钥和私钥，指定slat值(最好是越复杂越好)
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    /**
     * 读取公钥和私钥
     * @throws Exception
     */
    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 生成jwt类型的token，先读取公钥和私钥
     * @throws Exception
     */
    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    /**
     * 使用公钥解析token，获取用户信息
     * @throws Exception
     */
    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTYxNDIzNzU1MH0.dtap8RXDbjWmddeofoezZPV5a2xRMqHXvS__rwVlzHeS9ZMrQpAy2NA7O71GCWU8DGf2uDtK3te1ouLlzZoLhX-6Pl3Ov4USgPWH56fAmR3q8fWmk8_u0lnO5EdDNSkq97fdEdWNNG7nSBRVcPeyMDrNtPSQ31guGvexQk3u_oM";

        // 解析token，使用公钥
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}