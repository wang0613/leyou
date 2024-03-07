package com.leyou.page.page;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 测试生成HTML
 * @date 2021/2/15 15:05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PageServiceTest {

    @Autowired
    private PageService pageService;

    @Test
    public void createHtml() {
        pageService.createHtml(125L);

    }
}