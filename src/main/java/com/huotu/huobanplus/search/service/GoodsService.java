package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.search.model.view.ViewList;

import java.io.IOException;
import java.util.List;

/**
 * Created by lgh on 2016/8/1.
 */
public interface GoodsService {
    ViewList search(Long customerId, Integer pageSize, Integer pageNo,
                    String key, String brands, String category, String tags, Integer sorts);

    Long maxId() throws IOException;

    void update(Goods goods) throws IOException;

    void update(Long id) throws IOException;

    void update(Long merchantId,Long goodsId) throws IOException;

    void update(List<Goods> mallGoods) throws IOException;

    void updateByCustomerId(Long merchantId) throws IOException;
}
