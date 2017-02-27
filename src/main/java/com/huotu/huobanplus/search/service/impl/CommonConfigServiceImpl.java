package com.huotu.huobanplus.search.service.impl;

import com.huotu.huobanplus.search.service.CommonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/8/20.
 */
@Service
public class CommonConfigServiceImpl implements CommonConfigService {

    @Autowired
    private Environment env;

    @Override
    public String getSolrServerUrl() {
        return env.getProperty("solr.server.url", "http://localhost:8080/solr");
    }
}
