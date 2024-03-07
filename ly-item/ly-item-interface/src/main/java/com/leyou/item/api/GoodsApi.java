package com.leyou.item.api;

import com.leyou.common.vo.OrderDetail;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/1/31 19:40
 */
public interface GoodsApi {

    /**
     * 分页查询spu
     *
     * @param key      查询条件
     * @param saleable 是否上架
     * @param page     页码
     * @param rows     每页显示条数
     * @return PageResult<Spu>
     */
    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows);

    /**
     * 根据spuId查询对应的Spu信息
     *
     * @param spuId id
     * @return Spu
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long spuId);

    /**
     * 根据spu的id 查询对应的spu详情，用于页面的回显
     *
     * @param id spuID
     * @return SpuDetail
     */
    @GetMapping("spu/detail/{id}")
    SpuDetail querySpuDetailBySpuId(@PathVariable("id") Long id);

    /**
     * 根据spu的id 查询对应的sku集合，用于页面的回显
     *
     * @param spuId spuID
     * @return List<Sku>
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long spuId);


    /**
     * 根据skuID查询sku
     *
     * @param id skuID
     * @return sku
     */
    @GetMapping("sku/{id}")
    Sku querySkuById(@PathVariable("id") Long id);


    /**
     * 减库存操作
     * @param orderDetails 订单数据
     */
    @PostMapping("stock/decrease")
    void decreaseStock(List<OrderDetail> orderDetails);
}
