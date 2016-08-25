package com.huotu.huobanplus.search.boot;

import com.huotu.huobanplus.sdk.common.CommonClientSpringConfig;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.repository.solr.SolrHotRepository;
import com.huotu.huobanplus.search.service.CommonConfigService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import org.luffy.lib.libspring.logging.LoggingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
//@ImportResource(value = "classpath:spring-jpa.xml")
//@EnableJpaRepositories(value = {"com.huotu.huobanplus.search.repository.jpa"})
@Import(value = {LoggingConfig.class, CommonClientSpringConfig.class})
public class BootConfig {


    @Autowired
    private CommonConfigService commonConfigService;

//    @Bean
//    public MulticoreSolrClientFactory solrServerFactory() {
//        MulticoreSolrClientFactory multicoreSolrClientFactory = new MulticoreSolrClientFactory(solrClient(), "goods", "product");
//        return multicoreSolrClientFactory;
//    }

//    @Bean
//    public SolrClient solrClient() {
//        SolrClient solrClient = new HttpSolrClient("http://localhost:8983/solr");
//        return solrClient;
//
//        //采用内置服务器
////        EmbeddedSolrServerFactory factory = new EmbeddedSolrServerFactory("classpath:com/acme/solr");
////        return factory.getSolrClient();
//    }

//    @Autowired
//    private SolrTemplate solrProductTemplate;
//
//    @Bean(name = "solrProductTemplate")
//    public SolrTemplate solrProductTemplate() throws ParserConfigurationException, SAXException, IOException {
//        SolrClient solrClient = new HttpSolrClient(commonConfigService.getSolrServerUrl());
//        return new SolrTemplate(solrClient, "product");
//    }


    //
//    @Bean
//    public SolrProductRepository solrProductRepository() {
//        SolrProductRepository solrProductRepository = new SolrProductRepository();
//        solrProductRepository.setSolrOperations(solrProductTemplate);
//        return solrProductRepository;
//    }


    @Autowired
    private SolrTemplate solrGoodsTemplate;

    @Bean(name = "solrGoodsTemplate")
    public SolrTemplate solrGoodsTemplate() {
        SolrClient solrClient = new HttpSolrClient(commonConfigService.getSolrServerUrl());
        return new SolrTemplate(solrClient, "goods");
    }


    @Bean
    public SolrGoodsRepository solrGoodsRepository() {
        SolrGoodsRepository solrGoodsRepository = new SolrGoodsRepository();
        solrGoodsRepository.setSolrOperations(solrGoodsTemplate);
        return solrGoodsRepository;
    }


    @Autowired
    private SolrTemplate solrHotTemplate;

    @Bean(name = "solrHotTemplate")
    public SolrTemplate getSolrHotTemplate() {
        SolrClient solrClient = new HttpSolrClient(commonConfigService.getSolrServerUrl());
        return new SolrTemplate(solrClient, "hot");
    }

    @Bean
    public SolrHotRepository solrHotRepository() {
        SolrHotRepository solrHotRepository = new SolrHotRepository();
        solrHotRepository.setSolrOperations(solrHotTemplate);
        return solrHotRepository;
    }
}
