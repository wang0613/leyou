package com.leyou.order.web;

import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/3/3 21:28
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param order json格式对象
     * @return id 订单的id
     */
    @PostMapping("order")
    public ResponseEntity<Long> createOrder(@RequestBody @Valid Order order) {
        Long id = orderService.createOrder(order);

        System.out.println(order);
        //将订单id返回
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /**
     * 查询订单
     *
     * @param orderId 订单编号
     * @return Order
     */
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long orderId) {

        return ResponseEntity.ok(orderService.queryOrderById(orderId));
    }

    /**
     * 生成付款链接
     * @param orderId 订单id
     * @return string 付款链接
     */
    @GetMapping("url/{id}")
    public ResponseEntity<String> createPayUrl(@PathVariable("id") Long orderId){


        return ResponseEntity.ok(orderService.createPayUrl(orderId));
    }

    /**
     * 定时查询付款的状态
     * @param orderId 订单id
     * @return 1 付款成功，2 付款失败
     */
    @GetMapping("state/{id}")
    public ResponseEntity<Integer> queryPayState(@PathVariable("id") Long orderId){


        return ResponseEntity.ok(orderService.queryPayState(orderId));

    }



}
