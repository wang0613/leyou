package com.leyou.cart.serviec;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/3/2 13:30
 */
@Service
public class CartService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //key前缀
    public static final String KEY_PREFIX = "cart:uid";

    /**
     * 新增购物车数据,使用redis进行保存 Hash
     *
     * @param cart json对象
     */
    public void addCart(Cart cart) {
        //1.先判断是否有之前的数据，查询redis
        //第一层的key当前用户，从线程域中取出
        //因为从拦截器走到service使用一个线程，所以的用户是同一个
        UserInfo user = UserInterceptor.getUser();

        String key = user.getId() + KEY_PREFIX;
        //绑定用户的key
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //内层的key为商品id
        String hashKey = cart.getSkuId().toString();

        Integer num = cart.getNum();
        Long skuId = cart.getSkuId();

        //判断是否有数据
        //2.如果有数量+1，操作redis的num+ 传入的值
        if (operation.hasKey(hashKey)) {
            //存在，取出数据 进行+1
            String json = operation.get(hashKey).toString();
            //从redis中取出的json格式的字符串
            cart = JsonUtils.parse(json, Cart.class);
            //修改购物车数量， 从redis中读出的num+用户添加的数量
            cart.setNum(cart.getNum() + num);

        } else {
            //没有，往redis中写入数据
            // 不存在，新增购物车数据
            cart.setUserId(user.getId());
            // 其它商品信息，需要查询商品服务
            Sku sku = this.goodsClient.querySkuById(skuId);
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
        }

        //写回redis  存入的是string，取出的也是string
        operation.put(hashKey, JsonUtils.serialize(cart));


    }

    /**
     * 获取当前当前用户下的购物车数据
     *
     * @return List<Cart>
     */
    public List<Cart> getCartList() {

        //1.获取user
        UserInfo user = UserInterceptor.getUser();
        //外层key
        String key = user.getId() + KEY_PREFIX;

        if (!redisTemplate.hasKey(key)) {
            //没有，直接返回
            //key为null，没有数据直接返回
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        //2.绑定外层key
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(key);

        //3.获取所有的购物车数据
        List<Object> carts = ops.values();

        //4.使用流，将取出的jso格式，转换为对象并返回
        List<Cart> cartList = carts.stream().map(c -> JsonUtils.parse(c.toString(), Cart.class)).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(carts)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);

        }

        return cartList;

    }

    /**
     * 修改购物车商品的数量
     *
     * @param cart skuId和num
     */
    public void updateCart(Cart cart) {

        //1.获取线程域中的user

        UserInfo user = UserInterceptor.getUser();

        String key = user.getId() + KEY_PREFIX;
        //2.获取指定sku的数据 进行number的更新
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(key);

        //redis中只能拿出string的值  get之后转换为string，进行反序列化
        Cart cacheCart = JsonUtils.parse(ops.get(cart.getSkuId().toString()).toString(), Cart.class);

        cacheCart.setNum(cart.getNum());

        ops.put(cart.getSkuId().toString(), JsonUtils.serialize(cacheCart));

    }

    public void deleteCart(Long skuId) {


        //1.获取线程域中的user
        UserInfo user = UserInterceptor.getUser();

        String key = user.getId() + KEY_PREFIX;
        //2.获取指定sku的数据 进行number的更新
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(key);

        //删除指定key
        ops.delete(skuId.toString());
    }

    /**
     * 新增集合数据
     * @param cartList json集合
     */
    public void addCartList(List<Cart> cartList) {

        cartList.forEach(c->this.addCart(c));

    }
}
