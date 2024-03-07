package com.leyou.item.mapper;

import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * import tk.mybatis.mapper.additional.insert.InsertListMapper;
 * 该报下的批量新增不支持 主键自增
 * @author
 */
public interface StockMapper extends Mapper<Stock>, InsertListMapper<Stock>,
        SelectByIdListMapper<Stock, Long>, DeleteByIdListMapper<Stock,Long> {


    /**
     * 使用乐观锁的形式 更新库存
     * @param id skuID
     * @param num 数量
     * @return 1
     */
    @Update("update `tb_stock` set stock = stock - #{num} where sku_id = #{id} and stock > #{num}")
    int decreaseStock(@Param("id") Long id , @Param("num") Integer num);

}
