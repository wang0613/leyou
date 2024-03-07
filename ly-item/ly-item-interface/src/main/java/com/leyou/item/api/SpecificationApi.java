package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/1/31 20:09
 */

public interface SpecificationApi {

    /**
     * 根据组id或者分类id 查询对应的参数集合
     *
     * @param gid       规格组id
     * @param cid       分类的id
     * @param searching 是否搜索
     *                  两个参数，只要有一个就行，所以 required=false
     * @return List<SpecParam>规格参数集合
     */
    @GetMapping("spec/params")
    List<SpecParam> queryParamsList(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching);


    /**
     * 查询分类下的所有规格组
     * @param cid 分类id
     * @return List<SpecGroup>
     */
    @GetMapping("spec/groups")
    List<SpecGroup> querySpecGroupAndParamByCid(@RequestParam("cid") Long cid);
}