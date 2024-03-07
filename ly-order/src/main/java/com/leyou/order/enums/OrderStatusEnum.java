package com.leyou.order.enums;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/3/4 19:02
 */
public enum OrderStatusEnum {

    //OrderStatusEnum.UN_PAY.val()



    UN_PAY(1,"未支付"),
    PAYED(2,"已付款，未发货"),
    DELIVERED(3,"已发货，未确认"),
    SUCCESS(4,"已确认，未评价"),
    CLOSED(5,"已关闭，交易失败"),
    RATED(6,"已评价"),
    ;
    private int code;
    private String desc;


    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    //调用时返回当前的状态码
    public int val() {
        return code;
    }
}
