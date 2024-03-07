package com.leyou.order.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.IdWorker;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import com.leyou.order.utils.PayState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/3/3 21:35
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker idWorker; //雪花算法 生成订单编号

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PayHelper payHelper; //微信支付

    /**
     * 创建订单
     *
     * @param order json对象
     * @return id 订单id
     */
    @Transactional
    public Long createOrder(Order order) {

        int count = 0;

        //1. 新增订单
        //1.1 生成订单编号(雪花算法),基本信息
        long orderId = idWorker.nextId();

        //1.2 查询出用户的信息(线程域)
        UserInfo user = UserInterceptor.getUser();

        //初始化数据
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false); //未评价

        //1.3 设置收货人地址信息
        order.setOrderId(orderId);  //设置为生成的id
        order.setCreateTime(new Date());
        //1.4 计算金额(前端已计算)
        //1.5 保存order数据
        count = orderMapper.insertSelective(order); //选择性插入
        if (count != 1) {
            log.error("[订单微服务] 创建订单失败,orderId:{}", orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_FAIL);

        }


        //2. 新增订单详情
        // 订单详情中添加orderId  (一个订单中有多个sku)
        List<OrderDetail> details = order.getOrderDetails();
        details.forEach(o -> o.setOrderId(orderId));

        count = orderDetailMapper.insertList(order.getOrderDetails());
        if (count != details.size()) {
            log.error("[订单微服务] 创建订单详情失败,orderId:{}", orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_FAIL);

        }

        //3. 新增订单状态
        OrderStatus status = new OrderStatus();
        status.setOrderId(orderId); //和订单表一对一

        status.setCreateTime(order.getCreateTime());
//        status.setStatus(1); // 初始状态为未付款
        status.setStatus(OrderStatusEnum.UN_PAY.val());


        count = orderStatusMapper.insertSelective(status); //新增订单详情
        if (count != 1) {
            log.error("[订单微服务] 创建订单状态失败,orderId:{}", orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_FAIL);

        }

        //4. 减库存(暂时不做)
//        List<OrderDetail> details = order.getOrderDetails();


        return orderId;
    }

    /**
     * 根据订单id 查询订单
     *
     * @param orderId 订单id
     * @return Order
     */
    public Order queryOrderById(Long orderId) {

        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        //查询订单详情
        OrderDetail record = new OrderDetail();
        record.setOrderId(orderId);
        List<OrderDetail> details = orderDetailMapper.select(record);

        if (CollectionUtils.isEmpty(details)) {
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);

        }
        order.setOrderDetails(details);
        //查询订单状态
        OrderStatus status = orderStatusMapper.selectByPrimaryKey(orderId);
        if (status == null) {
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }

        order.setOrderStatus(status);


        return order;
    }

    /**
     * 生成付款链接
     *
     * @param orderId 订单id
     * @return url
     */
    public String createPayUrl(Long orderId) {

        String payUrl = payHelper.createPayUrl(orderId);

        if (StringUtils.isEmpty(payUrl)) {
            throw new LyException(ExceptionEnum.PAY_URL_FAIL);
        }

        return payUrl;

    }

    /**
     * 查询支付的状态
     *
     * @param orderId 订单id
     * @return 1 支付成功 2 支付失败
     */
    public Integer queryPayState(Long orderId) {

        PayState payState = payHelper.queryOrder(orderId);
        if (payState == null) {
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        return payState.getValue();
    }

    /**
     * 更新订单的支付状态
     *
     * @param orderId 订单id
     * @param i       订单status值
     */
    @Transactional
    public void updateStatus(Long orderId, int i) {

        OrderStatus record =new OrderStatus();
        record.setOrderId(orderId);
        record.setStatus(i);
        int count = orderStatusMapper.updateByPrimaryKeySelective(record);
        if (count != 1){
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_FAIL);
        }


    }
}
