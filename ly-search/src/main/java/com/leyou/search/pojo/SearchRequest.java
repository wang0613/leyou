package com.leyou.search.pojo;

import java.util.Map;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 搜索关键字
 * @date 2021/2/1 16:34
 */
public class SearchRequest  {


    private String key;// 搜索条件

    private Integer page;// 当前页

    private Map<String, String> filter; //可选的过滤字段

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    private static final int DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final int DEFAULT_PAGE = 1;// 默认页

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page); //取两个数之间的最大值
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }


}
