package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description:异常信息的枚举类
 * @author: haiLong_wang
 * @time: 2021/1/20 11:10
 */
@Getter
@AllArgsConstructor //全参构造方法
@NoArgsConstructor //无参构造
public enum ExceptionEnum {
//    enum：具有固定实例个数的类，枚举中的构造函数都是私有的
    //    private static final ExceptionEnum enums = new ExceptionEnum(400,"");在枚举中简化为以下形式

    PRICE_COUNT_BE_NULL(400, "价格不能为空"),
    CATEGORY_NOT_FOND(404, "商品分类没查到"),
    BRAND_QUERY_FAIL(404, "查询品牌没查到"),
    GOODS_DETAIL_QUERY_FAIL(404, "查询商品详情没查到"),
    GOODS_STOCK_QUERY_FAIL(404, "查询商品库存没查到"),
    BRAND_SAVE_FAIL(500, "新增品牌失败"),
    GOODS_SAVE_FAIL(500, "新增商品失败"),
    STOCK_SAVE_FAIL(500, "新增库存失败"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    GOODS_UPDATE_ERROR(500, "商品修改失败"),
    INVALID_FILE_TYPE(400, "无效的文件类型"),
    BRAND_NOT_FOND(404, "品牌未查到"),
    SPEC_GROUP_NOT_FOND(404, "商品规格组未查到"),
    SPEC_PARAMS_NOT_FOND(404, "商品规格参数未查到"),
    GOODS_NOT_FOND(404, "商品规格参数未查到"),
    INVALID_USER_DATA_TYPE(400,"用户数据类型不正确"),
    INVALID_VERIFY_CODE(400,"无效的验证码"),
    INVALID_PHONE(400,"无效的手机号"),
    INVALID_USERNAME_PASSWORD(400,"无效的用户名或密码"),
    CREATE_TOKEN_FAIL(500,"用户凭证生成失败"),
    UNAUTHORIZED(403,"未授权"),
    CART_NOT_FOUND(404,"购物车数据未找到"),
    CREATE_ORDER_FAIL(500,"创建订单失败"),
    STOCK_NOT_ENOUGH(500,"库存不足"),
    PAY_URL_FAIL(500,"生成付款链接失败"),
    ORDER_NOT_FOUND(404,"订单没有查询到"),
    ORDER_DETAIL_NOT_FOUND(404,"订单详情没有查询到"),
    ORDER_STATUS_NOT_FOUND(404,"订单状态没有查询到"),
    UPDATE_ORDER_STATUS_FAIL(500,"更新订单状态失败！"),
    ;
    private int code;
    private String msg;


}
