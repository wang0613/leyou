package com.leyou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 阿里云短信测试
 * @date 2021/2/21 22:15
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void test(){
        Map<String, String> map = new HashMap<>();
        map.put("phone","17633611196");
        map.put("code","1234");

        //使用mq发送异步消息，触发listener，发送验证码
        //指定exchange，因为没有在配置文件中指定，如果在配置文件中指定了交换机，只需要指定routingKey即可
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",map);

        try {
            Thread.sleep(10000L);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
