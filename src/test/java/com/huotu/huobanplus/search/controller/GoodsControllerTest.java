package com.huotu.huobanplus.search.controller;

import com.huotu.huobanplus.search.BaseTest;
import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.view.ViewList;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by helloztt on 2017-04-11.
 */
public class GoodsControllerTest extends BaseTest {
    @Autowired
    private SolrGoodsRepository solrGoodsRepository;

    private String baseUrl = "/goods";
    private Goods mockGoods;
    @Test
    public void searchIds() throws Exception {
        String controllerUrl = baseUrl + "/searchIds";
        mockGoods = mockGoodsWithRandomId();
        mockGoods.setBrandId(1L);
        solrGoodsRepository.save(mockGoods);
        //根据品牌搜索
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("brands", "|" + mockGoods.getBrandId() + "|"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordCount").value(1))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void search() throws Exception {
        customerId = 4886L;
        String controllerUrl = baseUrl + "/search";
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }

    @Test
    public void updateByMerchantIdAndGoodsId() throws Exception {

    }

}