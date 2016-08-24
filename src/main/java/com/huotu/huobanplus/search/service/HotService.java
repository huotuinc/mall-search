package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.search.model.solr.Hot;

import java.util.List;

/**
 * Created by Administrator on 2016/8/22.
 */
public interface HotService {
    List<String> suggest(Long customerId, String key, Integer pageSize);


    Hot save(Long customerId, String key);


    /**
     * 过滤搜索key
     * 去除 空格等特殊字符
     *
     * @param key
     * @return
     */
    String filterSearchKey(String key);
}
