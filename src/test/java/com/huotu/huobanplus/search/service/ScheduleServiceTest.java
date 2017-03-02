package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.UserRestRepository;
import com.huotu.huobanplus.search.BaseTest;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.repository.solr.SolrUserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by helloztt on 2017-02-28.
 */
public class ScheduleServiceTest extends BaseTest {
    @Autowired
    private SolrGoodsRepository solrGoodsRepository;
    @Autowired
    private SolrUserRepository solrUserRepository;
    @Autowired
    private GoodsRestRepository goodsRestRepository;
    @Autowired
    private UserRestRepository userRestRepository;
    @Autowired
    private ScheduleService scheduleService;

    @Test
    public void testAddGoods() throws IOException {
        customerId = 296L;
        solrGoodsRepository.deleteAll();
        long beforeCount = solrGoodsRepository.count();
        assertEquals(0, beforeCount);

        //设置为最大值,应该不增加数量
        scheduleService.goodsId = Long.MAX_VALUE;
        scheduleService.merchantId = customerId;
        scheduleService.addGoods();
        long afterAddMaxCount = solrGoodsRepository.count();
        assertEquals(beforeCount, afterAddMaxCount);


        long total = goodsRestRepository.searchByMerchantPK(customerId, new PageRequest(0, 1)).getTotalElements();
        scheduleService.goodsId = 0L;
        scheduleService.addGoods();
        long afterAddGoods = solrGoodsRepository.count();
        assertEquals(beforeCount + total, afterAddGoods);
    }

    @Test
    public void testAddUser() throws IOException{
        customerId = 296L;
        solrUserRepository.deleteAll();
        long beforeCount = solrUserRepository.count();
        assertEquals(0,beforeCount);

        //设置为最大值,应该不增加数量
        scheduleService.userId = Long.MAX_VALUE;
        scheduleService.merchantId = customerId;
        scheduleService.addUsers();
        long afterAddMaxCount = solrUserRepository.count();
        assertEquals(beforeCount, afterAddMaxCount);

        scheduleService.userId = 0L;
        long total = userRestRepository.findByMerchantId(customerId,null).getTotalElements();
        scheduleService.addUsers();
        long afterAddUser = solrUserRepository.count();
        assertEquals(beforeCount + total , afterAddUser);

    }

}