package com.leyou.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {
    /**
     * 你自己的accessKeyId
     */
    private String accessKeyId;

    /**
     * 你自己的AccessKeySecret
     */
    private String accessKeySecret;

    /**
     * 签名名称
     */
    private String signName;

    /**
     * 模板名称
     */
    private String verifyCodeTemplate;
}
