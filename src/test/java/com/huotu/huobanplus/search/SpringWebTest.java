package com.huotu.huobanplus.search;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by helloztt on 2017-02-28.
 */
public class SpringWebTest {
    /**
     * 自动注入应用程序上下文
     **/
    @Autowired
    protected WebApplicationContext context;
    /**
     * 自动注入servlet上下文
     **/
    @Autowired
    protected ServletContext servletContext;

    /**
     * mockMvc等待初始化
     **/
    protected MockMvc mockMvc;

    @Before
    public void initTest(){
        //初始化mockMvc
        this.createMockMVC();
    }

    protected void createMockMVC() {
        mockMvc = webAppContextSetup(context)
                .build();
    }
}
