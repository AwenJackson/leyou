package com.leyou.mq;

import com.leyou.service.GoodsService;
import org.elasticsearch.search.SearchService;
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
        private GoodsService goodsService;

    /**
     * 新增或修改（因为索引库的新增和修改方法是合二为一的，因此我们可以将这两类消息一同处理）
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "search.item.insert.queue"
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
        // 处理消息 , 对索引库实现新增或修改
        goodsService.listenInsertOrUpdate(spuId);
    }

    /**
     * 实现删除
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "search.item.insert.queue"
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
        // 处理消息 , 对索引库实现删除
        goodsService.deleteIndex(spuId);
    }

}
