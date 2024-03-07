package com.leyou.item.service;

import com.leyou.LyItemApplication;
import com.leyou.common.vo.OrderDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 测试减库存操作(乐观锁)
 * @date 2021/3/5 20:23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyItemApplication.class)
public class GoodsServiceTest {

    @Autowired
    private GoodsService goodsService;



    @Test
    public void decreaseStock() {

        OrderDetail detail = new OrderDetail();
        detail.setId(2600242L);
        detail.setNum(100);

        OrderDetail detail1 = new OrderDetail();
        detail1.setId(2600248L);
        detail1.setNum(200);

        //测试 OK
        List<OrderDetail> orderDetails = Arrays.asList(detail, detail1);


        goodsService.decreaseStock(orderDetails);

    }
}