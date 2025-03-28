package com.leyou.search.web;

import com.leyou.common.vo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/2/1 16:41
 */
@RestController
public class SearchController {


    @Autowired
    private SearchService searchService;

    /**
     * 搜索功能
     * @param request 查询条件对象参数
     * @return PageResult<Goods>
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request) {


        return ResponseEntity.ok(searchService.search(request));
    }
}
