package com.huotu.huobanplus.search.controller.impl;

import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.search.controller.GoodsController;
import com.huotu.huobanplus.search.model.enums.UpdateFrequency;
import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.view.ViewGoodsList;
import com.huotu.huobanplus.search.service.GoodsService;
import com.huotu.huobanplus.search.service.HotService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;


/**
 * Created by Administrator on 2016/8/15.
 */

@Controller
public class GoodsControllerImpl implements GoodsController {

    private static Log log = LogFactory.getLog(GoodsControllerImpl.class);

    @Autowired
    private GoodsService goodsService;


    @Autowired
    private HotService hotService;

    @Autowired
    private GoodsRestRepository goodsRestRepository;


    @Override
    public ViewGoodsList search(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "pageSize", required = false) Integer pageSize
            , @RequestParam(value = "pageNo", required = false) Integer pageNo
            , @RequestParam(value = "key", required = false) String key
            , @RequestParam(value = "brands", required = false) String brands
            , @RequestParam(value = "category", required = false) String category
            , @RequestParam(value = "tags", required = false) String tags
            , @RequestParam(value = "sorts", required = false) Integer sorts) {
        key = hotService.filterSearchKey(key);
        ViewGoodsList result = goodsService.search(customerId, pageSize, pageNo, key, brands, category, tags, sorts);
        //save hot
        if (!StringUtils.isEmpty(key)) hotService.save(customerId, key);
        return result;
    }

    @Override
    public List<String> suggest(@RequestParam(value = "customerId") Long customerId, @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestParam(value = "key") String key) {
        key = hotService.filterSearchKey(key);
        return hotService.suggest(customerId, key, pageSize);
    }


    @Override
    public void update(@RequestParam(value = "id") Long id, @RequestParam(value = "updateFrequency", required = false) UpdateFrequency updateFrequency) throws IOException {
        goodsService.update(id);
    }

    @Override
    @ResponseBody
    public String test() throws IOException {
        Pageable pageable = new PageRequest(0, 1000000);
        Page<com.huotu.huobanplus.common.entity.Goods> goodsPage = goodsRestRepository.findAll(pageable);
        goodsService.update(goodsPage.getContent());

        return "ok";
    }
}
