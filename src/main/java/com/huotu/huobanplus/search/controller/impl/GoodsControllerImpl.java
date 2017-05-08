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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;


/**
 * Created by Administrator on 2016/8/15.
 */

@Controller
@RequestMapping("/goods")
public class GoodsControllerImpl implements GoodsController {
    private static Log log = LogFactory.getLog(GoodsControllerImpl.class);

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private HotService hotService;

    @RequestMapping(value = "/searchIds", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public ViewList searchIds(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "supplierId") Long supplierId
            , @RequestParam(value = "ownId", defaultValue = "-1") Long ownId
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo
            , @RequestParam(value = "key", required = false) String key
            , @RequestParam(value = "brands", required = false) String brands
            , @RequestParam(value = "category", required = false) String category
            , @RequestParam(value = "typeIds", required = false) String typeIds
            , @RequestParam(value = "tags", required = false) String tags
            , @RequestParam(value = "sorts", required = false) String sorts) {
        key = hotService.filterSearchKey(key);
        ViewList result = goodsService.searchIds(customerId, supplierId, ownId, pageSize, pageNo, key, brands, category, typeIds, tags, sorts);
        //save hot
        if (!StringUtils.isEmpty(key)) {
            hotService.save(customerId, key);
        }
        return result;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public ViewList search(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "supplierId",required = false) Long supplierId
            , @RequestParam(value = "ownId", defaultValue = "-1") Long ownId
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo
            , @RequestParam(value = "key", required = false) String key
            , @RequestParam(value = "brands", required = false) String brands
            , @RequestParam(value = "category", required = false) String category
            , @RequestParam(value = "typeIds", required = false) String typeIds
            , @RequestParam(value = "tags", required = false) String tags
            , @RequestParam(value = "sorts", required = false) String sorts) {
        key = hotService.filterSearchKey(key);
        ViewList result = goodsService.search(customerId, supplierId, ownId, pageSize, pageNo, key, brands, category, typeIds, tags, sorts);
        //save hot
        if (!StringUtils.isEmpty(key)) {
            hotService.save(customerId, key);
        }
        return result;
    }

    @RequestMapping(value = "/suggest", method = RequestMethod.GET)
    @ResponseBody
    @Override
    public List<String> suggest(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "pageSize", required = false) Integer pageSize
            , @RequestParam(value = "key") String key) {
        key = hotService.filterSearchKey(key);
        return hotService.suggest(customerId, key, pageSize);
    }

    @RequestMapping(value = "/updateByMerchant", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public String updateByMerchantIdAndGoodsId(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "goodsId", required = false) Long goodsId) throws IOException {
        if (goodsId == null) {
            goodsService.updateByCustomerId(customerId);
        } else {
            goodsService.update(goodsId);
        }
        return "success";
    }

    @RequestMapping(value = "/updateByGoodsIds",method = RequestMethod.POST)
    @ResponseBody
    @Override
    public String updateByGoodsIds(@RequestParam(value = "goodsIds") String... goodsIds) throws IOException {
        if(goodsIds.length > 0){
            for(String goodsIdStr : goodsIds){
                Long goodsId = Long.parseLong(goodsIdStr);
                goodsService.update(goodsId);
            }
        }
        return "success";
    }
}
