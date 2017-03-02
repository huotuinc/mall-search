package com.huotu.huobanplus.search.repository.solr;

import com.huotu.huobanplus.search.BaseTest;
import com.huotu.huobanplus.search.model.solr.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by helloztt on 2017-02-28.
 */
public class SolrUserRepositoryTest extends BaseTest{
    @Autowired
    private SolrUserRepository solrUserRepository;

    @Test
    public void testInsertUser(){
        long beforeCount = solrUserRepository.count();
        User user = mockUser();
        solrUserRepository.save(user);
        long afterCount = solrUserRepository.count();
        assertEquals(beforeCount + 1 ,afterCount);
    }
}