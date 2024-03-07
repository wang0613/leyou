package com.leyou.search.client;

import com.leyou.item.pojo.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/1/31 19:27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {


    @Autowired
    private CategoryClient categoryClient;


    @Test
    public void queryCategoryByIds() {

        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(1L, 2L, 3L));
        categories.forEach(c-> System.out.println(c));
        Assert.assertEquals(3,categories.size());
    }
}