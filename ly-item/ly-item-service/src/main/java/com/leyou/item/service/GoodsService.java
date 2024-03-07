package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.OrderDetail;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 商品的业务逻辑
 * @author: haiLong_wang
 * @time: 2021/1/25 21:10
 */
@Service
@Slf4j
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private AmqpTemplate amqpTemplate;


    /**
     * 分页查询spu
     *
     * @param key      查询条件
     * @param saleable 是否上架
     * @param page     当前页码
     * @param rows     每页显示条数
     * @return PageResult中包含totalCount和list<spu>
     */
    public PageResult<Spu> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {

        //1、开始分页
        PageHelper.startPage(page, rows);

        //2、过滤
        Example example = new Example(Spu.class);

        Example.Criteria criteria = example.createCriteria();
        //条件过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //3、默认排序
        example.setOrderByClause("last_update_time DESC");

        //4、查询
        List<Spu> spus = spuMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(spus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }
        //解析分类的名称 和品牌的名称(进行转换)
        this.loadCategoryNameAndBrandName(spus);


        //5、解析分页的结果
        PageInfo<Spu> pageInfo = new PageInfo<Spu>(spus);

        return new PageResult<Spu>(pageInfo.getTotal(), spus);
    }

    /**
     * 将spu中的分类id和品牌的id 转换为分类名称和品牌名称
     *
     * @param spus 需要被转换的spu集合
     */
    private void loadCategoryNameAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //1、处理分类名称
            //使用category中定义的方法，通用mapper扩展方法
            List<Category> categories =
                    categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

//            categories.stream().map(c -> c.getName());
            List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
            //设置名称
            spu.setCname(StringUtils.join(names, "/")); //将集合中的数据以/进行拼接


            //2、处理品牌名称
            Long brandId = spu.getBrandId();

            Brand brand = brandService.queryById(brandId);
            spu.setBname(brand.getName());

        }

    }

    /**
     * 保存商品
     * spu和spuDetail进行绑定   spu有多个sku  每一个sku有一个stock
     *
     * @param spu 包含spuDetail，sku，stock
     */
    @Transactional
    public void saveGoods(Spu spu) {
        //1.新增spu
        //设置默认参数
        spu.setId(null);
        spu.setSaleable(true); //默认上架
        spu.setCreateTime(new Date()); //创建时间
        spu.setLastUpdateTime(spu.getCreateTime()); //最后的修改时间
        spu.setValid(false); //是否删除

        int count = spuMapper.insert(spu);

        if (count != 1) {
            //新增失败
            throw new LyException(ExceptionEnum.GOODS_SAVE_FAIL);
        }

        //2.新增spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        //设置默认参数
        spuDetail.setSpuId(spu.getId()); //新增完成的id（两张表共用一个主键）

        count = spuDetailMapper.insert(spuDetail);

        if (count != 1) {
            //新增失败
            throw new LyException(ExceptionEnum.GOODS_SAVE_FAIL);
        }

        //3.新增spu和stock
        saveSkuAndStock(spu);


        //发送mq消息
        this.sendMessage(spu.getId(), "insert");


    }

    /**
     * 新增spu和stock
     *
     * @param spu 前端的json对象
     */
    private void saveSkuAndStock(Spu spu) {
        int count;
        List<Stock> stocks = new ArrayList<>();

        //3.新增sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            //完成数据填充，设置默认参数
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());

            //注意：批量新增不能拿id
            count = skuMapper.insert(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_FAIL);
            }

            //stock库存和sku进行绑定

            //4.新增库存
            Stock stock = new Stock();
            //设置库存的默认参数
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock()); //设置库存

            //添加到提前定义好的集合中，进行批量新增
            stocks.add(stock);

        }

        //批量新增库存
        //需要手动在mapper上添加 InsertListMapper
        count = stockMapper.insertList(stocks);
        if (count != stocks.size()) {
            throw new LyException(ExceptionEnum.STOCK_SAVE_FAIL);
        }
    }

    /**
     * 根据spuID查询spuDetail详情
     *
     * @param id spuID
     * @return SpuDetail
     */
    public SpuDetail querySpuDetailBySpuId(Long id) {
        //spu和spuDetail共用一个主键
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);

        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.GOODS_DETAIL_QUERY_FAIL);
        }
        return spuDetail;

    }

    /**
     * 根据spuID 查询出对应的多个Sku和sku对应的stock
     *
     * @param spuId spuID
     * @return List<Sku>
     */
    public List<Sku> querySkuBySpuId(Long spuId) {
        //1.根据spuId查询出对应的sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);

        List<Sku> skuList = skuMapper.select(sku);

        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }


        //2.查询库存
//        for (Sku s : skuList) {
//            //sku和库存表共用一个id
//            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
//            if (stock == null) {
//                throw new LyException(ExceptionEnum.GOODS_STOCK_QUERY_FAIL);
//            }
//            //设置库存为  根据sku查询出的库存信息
//            s.setStock(stock.getStock());
//
//        }

        //2.查询库存
        //获取所有的id
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(ids)) {
            throw new LyException(ExceptionEnum.GOODS_STOCK_QUERY_FAIL);
        }

        //将sku中的库存设置为stock的库存
        //1.将库存变成一个map，key为Skuid，value为stock库存信息
        Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));

        //sku和skuId相同的
        skuList.forEach(s -> s.setStock(stockMap.get(s.getId())));

        return skuList;
    }

    /**
     * 修改spu
     *
     * @param spu pojo
     */
    @Transactional
    public void updateGoods(Spu spu) {
        //1、删除sku
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //查询以前的sku
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)) {
            //删除sku 以sku中spuID为条件
            skuMapper.delete(sku);

            //2、删除stock
            //拿到全部的id进行批量删除
            List<Long> skuIds = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            //批量删除库存
            stockMapper.deleteByIdList(skuIds);
        }

        //3、新增sku和stock
        this.saveSkuAndStock(spu);

        //4、更新spu
        spu.setLastUpdateTime(new Date()); //设置修改时间
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        int count = spuMapper.updateByPrimaryKeySelective(spu);//根据主键id进行更新
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

        //5、更新spuDetail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

        //发送mq消息
        this.sendMessage(spu.getId(), "update");

    }

    /**
     * 使用rabbitMQ，发送mq消息，这里要把所有异常都try起来，不能让消息的发送影响到正常的业务逻辑**
     *
     * @param id spuId
     */
    private void sendMessage(Long id, String type) {
        try {
            //修改成功之后，发送mq消息给搜索微服务和静态页微服务，在服务方注册Lister，称为消费者
            //根据指定routingKey，触发指定的listener
            amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception ex) {
            log.error("{}商品消息发送异常，商品id：{}", type, id, ex);
        }

    }

    /**
     * 根据spuId查询对应的Spu信息
     *
     * @param spuId id
     * @return spu
     */
    public Spu querySpuById(Long spuId) {

        //1、查询完spu，在同时查询一下sku，查询详情，一次性返回
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu == null) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }
        //2、查询sku（包括sku对应的库存）
        List<Sku> skuList = this.querySkuBySpuId(spuId);
        //为spu设置skuList
        spu.setSkus(skuList);

        //3、查询spuDetail
        SpuDetail spuDetail = this.querySpuDetailBySpuId(spuId);
        //为spu设置spuDetail
        spu.setSpuDetail(spuDetail);


        return spu;
    }

    /**
     * 根据sku的id集合查询sku
     *
     * @param ids 多个sku Id
     * @return list<Sku>
     */
    public List<Sku> querySkuByIds(List<Long> ids) {

        List<Sku> skuList = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_DETAIL_QUERY_FAIL);
        }
        //查询库存
        List<Long> skuIds = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stockList = stockMapper.selectByIdList(skuIds);

        //变为map，key为skuId value为库存
        Map<Long, Integer> map = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));

        //循环设置库存
        skuList.forEach(s -> s.setStock(map.get(s.getId()))
        );

        return skuList;
    }

    /**
     * 根据skuId查询SKu
     *
     * @param id skuID
     * @return Sku
     */
    public Sku querySkuById(Long id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据传入的skuId和num减少库存
     * 锁的问题：锁不住，需要使用分布式锁，zookeeper的实现原理就是 利用了节点的唯一性(即使zookeeper宕机，临时节点会自动清除)
     * 或者使用redis中setNx方法 创建成功返回1 失败返回0，(redis分布式锁有死锁问题，redis宕机需要手动清除)
     *
     * 我们认为锁的安全问题一定存在，在操作数据之前，现加锁 锁定，然后在修改称之为悲观锁！！(数据库需要加锁)
     * 同一时间段只能有一个用户操作，串行执行的性能非常差！！
     * 乐观锁：我们认为锁的安全问题不一定出现，不加锁，不做查询，不做判断，直接减库存
     * @param orderDetails 具体购买的商品
     */
    @Transactional
    public /*synchronized*/ void decreaseStock(List<OrderDetail> orderDetails) {
        //1.先利用zookeeper在某个目录中创建一个临时节点，成功就认为用户持有这把锁，创建失败就认为获取失败，直接返回，后者等待

        for (OrderDetail orderDetail : orderDetails) {

            //乐观锁就是上来就是减库存，不做查询，不做判断，在sql语句中判断，如果库存不足，sql语句就是失败！
//            update `tb_stock` set stock = stock - 1 where sku_id = 123 and stock > 1
            int count = stockMapper.decreaseStock(orderDetail.getId(), orderDetail.getNum());
            if (count != 1){
                //失败，抛出异常
                throw  new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }

        }
        //2.执行完成，删除此临时节点

    }
}
