package com.huotu.huobanplus.search.controller.impl;

import com.huotu.huobanplus.search.controller.GoodsController;
import com.huotu.huobanplus.search.model.view.ViewList;
import com.huotu.huobanplus.search.service.GoodsService;
import com.huotu.huobanplus.search.service.HotService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

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


    @Override
    public ViewList search(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo
            , @RequestParam(value = "key", required = false) String key
            , @RequestParam(value = "brands", required = false) String brands
            , @RequestParam(value = "category", required = false) String category
            , @RequestParam(value = "tags", required = false) String tags
            , @RequestParam(value = "sorts", required = false) Integer sorts) {
        key = hotService.filterSearchKey(key);
        ViewList result = goodsService.search(customerId, pageSize, pageNo, key, brands, category, tags, sorts);
        //save hot
        if (!StringUtils.isEmpty(key)) {
            hotService.save(customerId, key);
        }
        return result;
    }

    @Override
    public List<String> suggest(@RequestParam(value = "customerId") Long customerId, @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestParam(value = "key") String key) {
        key = hotService.filterSearchKey(key);
        return hotService.suggest(customerId, key, pageSize);
    }

    @Override
    @ResponseBody
    public String updateByMerchantIdAndGoodsId(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "goodsId", required = false) Long goodsId) throws IOException {
        if (goodsId == null) {
            goodsService.updateByCustomerId(customerId);
        } else {
            goodsService.update(customerId, goodsId);
        }
        return "success";
    }
}
