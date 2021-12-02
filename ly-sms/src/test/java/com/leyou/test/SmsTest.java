package com.leyou.test;

import com.leyou.config.SmsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTest {
    @Resource
    private AmqpTemplate amqpTemplate;

    @Test
    public void test() {
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code","15364268364");
    }

}
