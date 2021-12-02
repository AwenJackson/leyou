package com.leyou.mq;

import com.leyou.config.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SmsListener {
    @Resource
    private SmsUtils smsUtils;

    /**
     * 监听RabbitMQ消息，如果有消息发送请求那么就调用短信服务
     * @param phone
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "sms.verify.code.queue",declare = "true"
            ),
            exchange = @Exchange(
                    name = "ly.sms.exchange",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"
            ),
            key = "sms.verify.code"
    ))
    public void sendSms(String phone){
        if (StringUtils.isBlank(phone)) {
            return;
        }
        //处理消息
        smsUtils.sendSms(phone);
    }
}
