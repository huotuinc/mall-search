package com.huotu.huobanplus.search.service.impl;

import com.huotu.huobanplus.search.model.solr.Hot;
import com.huotu.huobanplus.search.repository.solr.SolrHotRepository;
import com.huotu.huobanplus.search.service.HotService;
import com.huotu.huobanplus.search.utils.PinyinUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/17.
 */
@Service
public class HotServiceImpl implements HotService {

    @Autowired
    private SolrHotRepository solrHotRepository;

    @Override
    public List<String> suggest(Long customerId, String key, Integer pageSize) {
        if (pageSize == null || pageSize <= 0) pageSize = 10;

        Page<Hot> hots = solrHotRepository.searchForStartsWith(customerId, key, pageSize);
        if (hots.getTotalElements() == 0) {
            hots = solrHotRepository.searchForComplex(customerId, key, pageSize);
        }


        List<String> list = new ArrayList<>();
        for (Hot hot : hots) {
            list.add(hot.getName());
        }
        return list;
    }

    @Override
    public Hot save(Long customerId, String key) {
        Hot hot = solrHotRepository.findOne(customerId + "_" + key);
        if (hot == null) {
            hot = new Hot();
            hot.setId(customerId + "_" + key);
            hot.setName(key);
            hot.setPinyin(PinyinUtils.getFullSpell(key));
            hot.setHot(1L);
            hot = solrHotRepository.save(hot);
        } else {
            hot.setHot(hot.getHot() + 1);
            hot = solrHotRepository.save(hot);
        }
        return hot;
    }

    @Override
    public String filterSearchKey(String key) {
        if (!StringUtils.isEmpty(key))
            key = key.replace(" ", "");
        return key;
    }
}
