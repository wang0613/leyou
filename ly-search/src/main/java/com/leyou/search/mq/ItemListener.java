package com.leyou.search.mq;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: mq消费者
 * @date 2021/2/19 21:30
 */
@Component
public class ItemListener {

    @Autowired
    private SearchService searchService;


    /**
     * 在es中新增和修改合二为一
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.insert.queue",durable = "true"),
            exchange =@Exchange(name = "ly.item.exchange"
            , type = ExchangeTypes.TOPIC),
            key={"item.insert","item.update"}
    ))
    public void listenInsertOrUpdate(Long spuId){
        if (spuId == null){
            return;
        }
        //处理消息，对索引库经进行修改或修改
        searchService.createOrUpdateIndex(spuId);

    }

    /**
     * 监听routingKey为
     * @param spuId 商品id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.delete.queue",durable = "true"),
            exchange =@Exchange(name = "ly.item.exchange"
                    , type = ExchangeTypes.TOPIC),
            key="item.delete"
    ))
    public void listenDelete(Long spuId){
        if (spuId == null){
            return;
        }
        //处理消息，对索引库经进行删除
        searchService.deleteIndex(spuId);
    }


}
