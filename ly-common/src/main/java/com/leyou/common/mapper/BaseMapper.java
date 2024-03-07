package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;


/**
 * 自定义通用mapper
 * @param <T>
 */

@RegisterMapper //注册到mapper，才能使用
public interface BaseMapper<T> extends Mapper<T>, IdListMapper<T,Long>, InsertListMapper<T> {

    /*
    批量新增其实有两个：
    tk.mybatis.mapper.common.special.InsertListMapper包下的insertList()方法：
       使用该方法的实体类主键必须是自增的（需要在实体类中指出）。

    tk.mybatis.mapper.additional.insert.InsertListMapper包下的insertList()方法：
       该方法不支持主键策略，需要在实体类中指定主键。该方法执行后不会回写实体类的主键值。

     */
}
