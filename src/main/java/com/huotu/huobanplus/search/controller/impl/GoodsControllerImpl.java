package com.huotu.huobanplus.search.controller.impl;

import com.huotu.huobanplus.search.controller.GoodsController;
import com.huotu.huobanplus.search.model.enums.UpdateFrequency;
import com.huotu.huobanplus.search.model.view.ViewGoodsList;
import com.huotu.huobanplus.search.service.GoodsService;
import com.huotu.huobanplus.search.service.HotService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ViewGoodsList search(@RequestParam(value = "customerId") Long customerId, @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "levelId", required = false) Integer levelId, @RequestParam(value = "key", required = false) String key, @RequestParam(value = "brandsId", required = false) Integer brandsId, @RequestParam(value = "categoryId", required = false) Integer categoryId, @RequestParam(value = "hotspot", required = false) String hotspot, @RequestParam(value = "sorts", required = false) Integer sorts) {
        key = hotService.filterSearchKey(key);
        ViewGoodsList result = goodsService.search(customerId, pageSize, page, levelId, key, brandsId, categoryId, hotspot, sorts);
        //save hot
        hotService.save(customerId, key);
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
    public String test() {
        log.info("entering");
        return "abc";
    }
}