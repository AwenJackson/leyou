package com.leyou.props;

import com.leyou.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private String secret; //登录校验的密钥
    private String pubKeyPath; // 公钥地址
    private String priKeyPath; // 私钥地址
    private Integer expire; //过期时间,单位分钟
    private String cookieName;  // cookie请求名称

    @PostConstruct
    public void initData() throws Exception {
        File pubPath = new File(pubKeyPath);
        if (!pubPath.exists()) {
            // 生成公钥私钥
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
        }
        // 读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }
}
