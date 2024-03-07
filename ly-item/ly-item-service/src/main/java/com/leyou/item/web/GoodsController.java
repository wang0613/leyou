package com.leyou.item.web;

import com.leyou.common.vo.OrderDetail;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 商品
 * @author: haiLong_wang
 * @time: 2021/1/25 21:10
 */
@RestController
public class GoodsController {


    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询spu
     * @param key      查询条件
     * @param saleable 是否上架
     * @param page     页码
     * @param rows     每页显示条数
     * @return PageResult<Spu>
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows) {


        return ResponseEntity.ok(goodsService.querySpuByPage(key, saleable, page, rows));
    }

    /**
     * 商品的新增
     * @param spu json格式字符串 包含spuDetail，sku，stock
     * @return 无返回值类型
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {

        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spu的id 查询对应的spu详情，用于页面的回显
     * @param id spuID
     * @return SpuDetail
     */
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("id") Long id) {

        return ResponseEntity.ok(goodsService.querySpuDetailBySpuId(id));
    }

    /**
     * 根据spu的id 查询对应的sku集合，用于页面的回显
     * @param spuId spuID
     * @return List<Sku>
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));
    }

    /**
     * 修改商品属性（spu，spuDetail，sku，stock）
     * @param spu 商品信息
     * @return 无
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu) {
        goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spuId查询对应的Spu信息
     * @param spuId 商品id
     * @return Spu
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long spuId) {

        return ResponseEntity.ok(goodsService.querySpuById(spuId));
    }

    /**
     * 根据sku的id集合查询sku
     * @param ids 多个sku Id
     * @return list<Sku>
     */
    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuByIds(
            @RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(goodsService.querySkuByIds(ids));
    }

    /**
     * 根据skuID查询sku
     * @param id skuID
     * @return sku
     */
    @GetMapping("sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id")Long id){
        Sku sku = this.goodsService.querySkuById(id);
        if (sku == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(sku);
    }


    /**
     * 减库存(使用乐观锁)
     * @param orderDetails 购买的商品
     * @return 无
     */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(List<OrderDetail> orderDetails){
        goodsService.decreaseStock(orderDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
