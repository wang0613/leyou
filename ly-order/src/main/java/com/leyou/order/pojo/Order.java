package com.leyou.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author 订单表
 * 一对多订单详情
 * 一对一订单状态表
 */
@Data
@Table(name = "tb_order")
public class Order {

    //不做自增长是因为订单数据非常庞大，一定需要分库分表，需要保证唯一id，不能使用数据库的自增长
    //使用雪花算法，生成唯一id
    @Id
    private Long orderId;// 订单id
    @NotNull //校验不能为null
    private Long totalPay;// 总金额
    @NotNull
    private Long actualPay;// 实付金额
    @NotNull
    private Integer paymentType; // 支付类型，1、在线支付，2、货到付款

    private String promotionIds; // 参与促销活动的id
    private Long postFee = 0L;// 邮费
    private Date createTime;// 创建时间
    private String shippingName;// 物流名称
    private String shippingCode;// 物流单号

    private Long userId;// 用户id
    private String buyerMessage;// 买家留言
    private String buyerNick;// 买家昵称
    private Boolean buyerRate;// 买家是否已经评价  false 0未评价，true 1已评价


    private String receiver; // 收货人全名
    private String receiverMobile; // 移动电话
    private String receiverState; // 省份
    private String receiverCity; // 城市
    private String receiverDistrict; // 区/县
    private String receiverAddress; // 收货地址，如：xx路xx号
    private String receiverZip; // 邮政编码,如：310001
    private Integer invoiceType = 0;// 发票类型，0无发票，1普通发票，2电子发票，3增值税发票
    private Integer sourceType = 1;// 订单来源 1:app端，2：pc端，3：M端，4：微信端，5：手机qq端


    //非数据库字段 orderDetail
    @Transient
    private List<OrderDetail> orderDetails; //对应的订单详情

    //非数据库字段  orderStatus
    @Transient
    private OrderStatus orderStatus; //1、未付款 2、已付款,未发货 3、已发货,未确认 4、交易成功 5、交易关闭 6、已评价'


}
