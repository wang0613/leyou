package com.leyou.page.mq;

import com.leyou.page.page.PageService;
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
 * @description:
 * @date 2021/2/19 22:04
 */
@Component
public class PageListener {

    @Autowired
    private PageService pageService;


    /**
     * 在es中新增和修改合二为一
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.insert.queue",durable = "true"),
            exchange =@Exchange(name = "ly.item.exchange"
                    , type = ExchangeTypes.TOPIC),
            key={"item.insert","item.update"}
    ))
    public void listenInsertOrUpdate(Long spuId){
        if (spuId == null){
            return;
        }
        //处理消息，新建静态页
        pageService.createHtml(spuId);

    }

    /**
     * 监听routingKey为
     * @param spuId 商品id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.delete.queue",durable = "true"),
            exchange =@Exchange(name = "ly.item.exchange"
                    , type = ExchangeTypes.TOPIC),
            key="item.insert"
    ))
    public void listenDelete(Long spuId){
        if (spuId == null){
            return;
        }
        //处理消息，对静态页进行删除
        pageService.deleteHtml(spuId);

    }

}
