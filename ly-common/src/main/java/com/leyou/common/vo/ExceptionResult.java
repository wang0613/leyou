package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;
//作用于pojo表示层 叫做vo
//一般用于web层向view中封装并提供需要展示的数据
/**
 * 异常结果对象
 */
@Data
public class ExceptionResult {

    private int status; //状态码
    private String message; //消息
    private Long timestamp; //时间戳

    //内部接收枚举类型，使用枚举为结果异常对象赋值
    public ExceptionResult(ExceptionEnum em) {
        this.status = em.getCode();
        this.message = em.getMsg();
        this.timestamp = System.currentTimeMillis();

    }

}
