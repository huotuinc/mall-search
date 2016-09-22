package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.search.model.view.ViewGoodsList;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Created by lgh on 2016/8/1.
 */
public interface GoodsService {
    ViewGoodsList search(Long customerId, Integer pageSize, Integer page, Integer levelId
            , String key, String brands, String category, String hotspot
            , Integer sorts);

    void update(Long id) throws IOException;
}
