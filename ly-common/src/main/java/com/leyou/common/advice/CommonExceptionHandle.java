package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 通用异常处理器
 */
@ControllerAdvice //默认情况下，自动拦截所有的controller请求，
//注意启动类的包扫描：(使用此处理器的启动类)
public class CommonExceptionHandle {


    /**
     * 通用异常处理方法：当抛出RuntimeException执行此方法
     * 捕获自定义异常
     */
    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleException(LyException e) {

        //抛出异常时，将异常以参数的形式传递，抛出的异常信息，以message的形式存储
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        //使用枚举类型获取code
        ExceptionEnum em = e.getExceptionEnum();
        //创建异常结果对象 内部封装 异常信息，状态码和时间戳
        return ResponseEntity.status(em.getCode()).body(new ExceptionResult(em));
    }



}
