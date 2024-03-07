package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 规格参数
 * @author: haiLong_wang
 * @time: 2021/1/20 1:10
 */
@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;


    /**
     * 根据分类id 查询规格组
     * @param cid 分类id
     * @return List<SpecGroup>
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> specGroups = specificationService.queryGroupsByCid(cid);

        return ResponseEntity.ok(specGroups);
    }

    /**
     * 根据组id或者分类id 查询对应的参数集合
     * @param gid 规格组id
     * @param cid 分类的id
     * @param searching 是否搜索
     * 两个参数，只要有一个就行，所以 required=false
     * @return List<SpecParam>规格参数集合
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamsList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching) {

        return  ResponseEntity.ok(specificationService.queryParamsList(gid,cid,searching));
    }

    /**
     * 查询分类下的所有规格组以及组内的Param
     * @param cid 分类id
     * @return List<SpecGroup>
     */
    @GetMapping("groups")
    public ResponseEntity<List<SpecGroup>> querySpecGroupAndParamByCid( @RequestParam("cid") Long cid){

        return ResponseEntity.ok(specificationService.querySpecGroupAndParamByCid(cid));
    }
}
