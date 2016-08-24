package com.huotu.huobanplus.search;

import com.huotu.huobanplus.search.boot.BootConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */

//@WebAppConfiguration
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BootConfig.class})
public class SolrTest extends BaseTest {

    @Test
    public void test() throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode("#","utf-8"));
        System.out.println(URLEncoder.encode("{","utf-8"));
        System.out.println(URLEncoder.encode("}","utf-8"));

    }
}
