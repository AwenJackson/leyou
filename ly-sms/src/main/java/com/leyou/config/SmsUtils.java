package com.leyou.config;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.leyou.common.utils.NumberUtils;
import com.leyou.prop.SmsProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务类
 */
@Component
@EnableConfigurationProperties(value = SmsProperties.class)
public class SmsUtils {
    @Resource
    private SmsProperties smsProperties;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 处理限流问题 (设置前缀)
    static final String KEY_PREFIX = "sms:phone:";
    static final String KEY_CODE_PREFIX = "sms:code:phone:";
    static final long SMS_MIN_INTERVAL_IN_MILLIS = 600000; //  60 秒

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    private com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 发送短信验证码rows
     * @param phone
     */
    public void sendSms(String phone){
        String key = KEY_PREFIX +phone;
        String key_code = KEY_CODE_PREFIX + phone;
        SendSmsResponse sendSmsResponse = null;
        try {
            com.aliyun.dysmsapi20170525.Client client = this
                    .createClient(smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret());
            // 控制验证码发送的频率
            String last = stringRedisTemplate.opsForValue().get(key);
            if(StringUtils.isNotBlank(last)){
                // 1.取出发送验证码的 时间
                Long lastTime = Long.valueOf(last);
                // 2.获取现在的时间
                Long nowTime = System.currentTimeMillis();
                if(nowTime-lastTime < SMS_MIN_INTERVAL_IN_MILLIS){  // 还没有达到60秒 再次发送了 请求 = 频率过快
                    // 发送频率过快
                    System.out.println("发送频率过快,已拦截:"+phone);
                    return ;
                }
            }
            //生成随机6位数验证码
            String code = NumberUtils.generateCode(6);
            // 存储当前生成的验证码
            // redis
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(smsProperties.getSignName())
                    .setTemplateCode(smsProperties.getVerifyCodeTemplate())
                    .setTemplateParam("{\"code\":\""+code+"\"}");
            // 复制代码运行请自行打印 API 的返回值
//             sendSmsResponse = client.sendSms(sendSmsRequest);
            client.sendSms(sendSmsRequest);
            System.out.println("code = " + code);
            // 存储保存时长设置为1分钟(储存验证码所发送的时间)
            stringRedisTemplate.opsForValue().set(key,String.valueOf(System.currentTimeMillis()),1, TimeUnit.MINUTES);
            // 将验证码存放到redis 5(分钟)
            stringRedisTemplate.opsForValue().set(key_code,code,5, TimeUnit.MINUTES);
            System.out.println(stringRedisTemplate.opsForValue().get(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
