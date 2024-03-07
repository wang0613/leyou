package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.serviec.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 后台购物车
 * @date 2021/3/2 12:20
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;


    /**
     * 新增当前用户下的购物车
     *
     * @param cart Json对象，包含skuId和num属性
     * @return 无
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {

        cartService.addCart(cart);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 获取当前用户下的购物车数据
     *
     * @return List<Cart>
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> getCartList() {


        return ResponseEntity.ok(cartService.getCartList());
    }

    /**
     * 修改购物车商品的数量
     *
     * @param cart skuId和num
     * @return 无
     */
    @PutMapping
    public ResponseEntity<Void> updateCart(@RequestBody Cart cart) {
        cartService.updateCart(cart);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 删除购物车数据
     *
     * @param skuId 商品id
     * @return 无
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId) {

        cartService.deleteCart(skuId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 新增本地数据
     * @param cartList json集合
     * @return 无
     */
    @PostMapping("addlist")
    public ResponseEntity<Void> addLocalCart(@RequestBody List<Cart> cartList) {
        cartService.addCartList(cartList);
        return ResponseEntity.noContent().build();
    }

}
