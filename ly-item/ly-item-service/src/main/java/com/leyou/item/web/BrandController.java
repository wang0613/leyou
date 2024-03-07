package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 品牌管理
 * @author: haiLong_wang
 * @time: 2021/1/12 21:10
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 分页查询品牌
     *
     * @param page   当前页码
     * @param rows   条数
     * @param sortBy 排序方式
     * @param desc   是否是降序
     * @param key    查询条件
     * @return PageResult<Brand>
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key) {

        return ResponseEntity.ok(brandService.queryBrandByPage(page, rows, sortBy, desc, key));
    }

    /**
     * 品牌的新增
     *
     * @param brand json
     * @param cids  属于哪一个分类下的品牌
     * @return 无
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {

        brandService.saveBrand(brand, cids);
        //如果有返回值，就选body，没有返回值 就选build
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 修改品牌
     *
     * @param brand json对象
     * @param cids 分类id
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {

        brandService.updateBrand(brand, cids);
        //如果有返回值，就选body，没有返回值 就选build
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    /**
     * 查询指定分类下的品牌
     *
     * @param cid 分类id
     * @return List<Brand>
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid) {

        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    /**
     * 根据品牌id 查询品牌
     * @param id 品牌id
     * @return Brand
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(brandService.queryById(id));

    }

    /**
     * 根据id查询Brand
     * @param ids 批量查询
     * @return List<Brand>
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandListById(@RequestParam("ids") List<Long> ids){


        return ResponseEntity.ok(brandService.queryByIds(ids));
    }


}
