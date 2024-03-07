package com.leyou.item.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: restful风格演示
 * @author: haiLong_wang
 * @time: 2021/1/10 11:10
 */
@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService service;

    /**
     * Restful风格中使用 ResponseEntity<T>内部封装需要返回的结果和状态码
     * @param item 新增对象
     * @return Item返回新增成功的对象
     */
    @PostMapping
    public ResponseEntity<Item> saveItem(Item item) {

        //校验价格
        if (item.getPrice() == null) {
            //400错误的请求，返回null
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            //1.0 bug:没有相应信息 虽然状态码是400

            //不能抛异常 状态码500  抛异常是服务器内部代码错误
            //2.0:使用环绕通知拦截请求   (价格不能为空  存储到了异常的message中)
//            throw new RuntimeException("价格不能为空");

            //3.0使用自定义异常，使用构造传递enum类型
            throw new LyException(ExceptionEnum.PRICE_COUNT_BE_NULL);

        }
        //保存
        item = service.saveItem(item);

        //201代表创建成功 == HttpStatus.CREATED
        //body中存储返回结果
        return ResponseEntity.status(HttpStatus.CREATED).body(item);

    }

}
