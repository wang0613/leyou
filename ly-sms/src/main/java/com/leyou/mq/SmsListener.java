package com.leyou.mq;

import com.leyou.common.utils.JsonUtils;
import com.leyou.conf.SmsProperties;
import com.leyou.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 监听mq消息，实现异步短信的发送
 * @date 2021/2/21 21:44
 */
@Component
@EnableConfigurationProperties(SmsProperties.class)
@Slf4j
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils; //短信工具类

    @Autowired
    private SmsProperties props;

    /**
     * 发送短息验证码
     * @param msg (Map<String, String>存放手机号和模板参数,
     *            因为rabbitMq只能传递一个参数，封装map用于传输
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sms.verify.code.queue", durable = "true"),
            exchange = @Exchange(name = "ly.sms.exchange",
                    type = ExchangeTypes.TOPIC),
            key = {"sms.verify.code"}
    ))
    public void listenSms(Map<String, String> msg) {

        if (CollectionUtils.isEmpty(msg)) {
            return;
        }
        //如果没有指定手机号，结束
        String phone = msg.remove("phone");  //remove删除并返回指定的元素
        if (StringUtils.isBlank(phone)) {
            return;
        }

        //模板的参数，必须为json格式
        //map中只有两个元素，phone和code，删除phone之后，只有code，直接将map转换为json格式
        smsUtils.sendSms(phone, props.getSingName(), props.getVerifyCodeTemplate(), JsonUtils.serialize(msg));

    }
}
