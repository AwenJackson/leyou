package com.leyou.test;

import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@SpringBootTest
public class JwtTest {
    private static final String pubKeyPath = "C:\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "C:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void test_rsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath,priKeyPath,"234");
    }

    @Before
    public void test_getRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void test_token() {
        //生成Token
        UserInfo userInfo = new UserInfo(1L, "Jack");
        String token = JwtUtils.generateToken(userInfo, privateKey, 5);
        System.out.println("token = " + token);
        //eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJKYWNrIiwiZXhwIjoxNjM3NTcxMDA4fQ.C8SceIGxeIORA3hTYFE9Hulh4oI01GXty4UZ3d3Q82xKLUgCqXs1Qt7y6Wut3wjLTZrbVIgLJnwNixhwOLpabJyZ7EVmC65PD_Agv0t5dCVeI7E-YmMQ6ksED3Akcot69X3BmGR_C9OUQZJ921x_5O6Xlgm0C_5HyFCPM5N9uwQ
    }

    @Test
    public void test_getToken() throws Exception {
        //解析Token
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJKYWNrIiwiZXhwIjoxNjM3NTcxMDA4fQ.C8SceIGxeIORA3hTYFE9Hulh4oI01GXty4UZ3d3Q82xKLUgCqXs1Qt7y6Wut3wjLTZrbVIgLJnwNixhwOLpabJyZ7EVmC65PD_Agv0t5dCVeI7E-YmMQ6ksED3Akcot69X3BmGR_C9OUQZJ921x_5O6Xlgm0C_5HyFCPM5N9uwQ";
        // 解析token
        UserInfo user = JwtUtils.getUserInfo(publicKey,token);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getName());
    }
}
