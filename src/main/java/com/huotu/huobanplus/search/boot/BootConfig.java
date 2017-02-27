package com.huotu.huobanplus.search.boot;

import com.huotu.huobanplus.sdk.common.CommonClientSpringConfig;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.repository.solr.SolrHotRepository;
import com.huotu.huobanplus.search.service.CommonConfigService;
import me.jiangcai.lib.spring.logging.LoggingConfig;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


/**
 * Created by lgh on 2016/8/1.
 */

@Configuration
@ComponentScan({"com.huotu.huobanplus.search.service", "com.huotu.huobanplus.search.controller"})
@EnableSolrRepositories(schemaCreationSupport = true, multicoreSupport = true)
@Import(value = {LoggingConfig.class, CommonClientSpringConfig.class})
public class BootConfig {


    @Autowired
    private CommonConfigService commonConfigService;
    @Autowired
    private SolrTemplate solrGoodsTemplate;
    @Autowired
    private SolrTemplate solrHotTemplate;

    //spring-data-solr多核处理暂时不支持自动获取core，目前暂时手动加载
    @Bean(name = "solrGoodsTemplate")
    public SolrTemplate solrGoodsTemplate() throws ParserConfigurationException, SAXException, IOException {
        return new SolrTemplate(solrClient(), "goods");
    }

    @Bean(name = "solrHotTemplate")
    public SolrTemplate solrHotTemplate() throws ParserConfigurationException, SAXException, IOException {
        return new SolrTemplate(solrClient(), "hot");
    }

    private SolrClient solrClient() throws IOException, SAXException, ParserConfigurationException {
        return new HttpSolrClient(commonConfigService.getSolrServerUrl());
    }

    @Bean
    public SolrGoodsRepository solrGoodsRepository() {
        SolrGoodsRepository solrGoodsRepository = new SolrGoodsRepository();
        solrGoodsRepository.setSolrOperations(solrGoodsTemplate);
        return solrGoodsRepository;
    }

    @Bean
    public SolrHotRepository solrHotRepository() {
        SolrHotRepository solrHotRepository = new SolrHotRepository();
        solrHotRepository.setSolrOperations(solrHotTemplate);
        return solrHotRepository;
    }


}
