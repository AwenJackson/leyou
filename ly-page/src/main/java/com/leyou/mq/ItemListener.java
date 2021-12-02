package com.leyou.mq;

import com.leyou.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ItemListener {
    @Resource
    private PageService pageService;

    /**
     * 创建或修改静态页
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "page.item.insert.queue"
//                    durable = "true"
            ),
            exchange = @Exchange(
                    name = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true"
//                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.insert", "item.update"}
    ))
    public void listenInsertOrUpdate(Long spuId) {
        if(spuId == null){
            return ;
        }
        // 处理消息 , 创建静态页
        pageService.asyncExcute(spuId);
    }

    /**
     * 删除静态页
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "page.item.insert.queue"
//                    durable = "true"
            ),
            exchange = @Exchange(
                    name = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true"
//                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.delete"}
    ))
    public void listenDelete(Long spuId) {
        if(spuId == null){
            return ;
        }
        // 处理消息 , 对静态页实现删除
        pageService.deleteHtml(spuId);
    }
}
