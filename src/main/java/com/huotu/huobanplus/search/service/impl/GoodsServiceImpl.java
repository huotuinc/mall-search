package com.huotu.huobanplus.search.service.impl;

import com.huotu.huobanplus.search.entity.MallGoods;
import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.view.Paging;
import com.huotu.huobanplus.search.model.view.ViewGoods;
import com.huotu.huobanplus.search.model.view.ViewGoodsList;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lgh on 2016/8/1.
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SolrGoodsRepository solrGoodsRepository;


    @Override
    public ViewGoodsList search(Long customerId, Integer pageSize, Integer page, Integer levelId
            , String key, Integer brandsId, Integer categoryId, String hotspot, Integer sorts) {
        //todo  think levelId
        //data check
        if (customerId == null) throw new IllegalArgumentException();
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (page == null || page <= 0) page = 0;

        Page<Goods> goodses = solrGoodsRepository.search(customerId, pageSize, page, levelId, key, brandsId, categoryId, hotspot, sorts);

        ViewGoodsList viewGoodsList = new ViewGoodsList();
        viewGoodsList.setPaging(new Paging(pageSize, page, goodses.getTotalElements()));
        List<ViewGoods> viewGoodses = new ArrayList<>();
        for (Goods goods : goodses) {
            ViewGoods viewGoods = new ViewGoods();
            viewGoods.setId(goods.getId());
            viewGoods.setTitle(goods.getTitle());
            viewGoods.setPictureUrl(goods.getPictureUrl());
            viewGoods.setPrice(goods.getPrice());
            viewGoods.setOriginalPrice(goods.getOriginalPrice());
            viewGoodses.add(viewGoods);
        }
        viewGoodsList.setList(viewGoodses);

        return viewGoodsList;
    }

    public void update(Long id) throws IOException {
        com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(id);
        if (mallGoods != null) {
            Goods goods = solrGoodsRepository.findOne(id);
            if (goods == null) {
                goods = new Goods();
                goods = mallGoodsToSolrGoods(mallGoods, goods);
                solrGoodsRepository.save(goods);
            } else {
                goods = mallGoodsToSolrGoods(mallGoods, goods);
                solrGoodsRepository.save(goods);
            }
        }
    }

    public Goods mallGoodsToSolrGoods(MallGoods mallGoods, Goods goods) {


        goods.setId(mallGoods.getId());
        goods.setCustomerId(mallGoods.getCustomerId());
        goods.setTitle(mallGoods.getTitle());
        goods.setPrice(mallGoods.getPrice());
        goods.setOriginalPrice(mallGoods.getMarketPrice());
        goods.setMemberPrice(mallGoods.getPrice());//todo
        goods.setPictureUrl(mallGoods.getSmallPic());
        goods.setDescription(mallGoods.getDescription());
        //MallGoodsKeywords
        goods.setKeyword();
        goods.setSupplier(mallGoods.getSupplier().getName());
        goods.setTags("标签" + id);
//        goods.setVirturalCatetory("虚拟分类");
        goods.setBrandsId(mallGoods.getBrandId());
        goods.setCategoryId(mallGoods.getCategoryId());
        goods.setHotspot("");
        goods.setUpdateTime(mallGoods.getCreateTime());
        goods.setSales((long) mallGoods.getSalesCount());
        goods.setPriceDesc("");

        return goods;
    }
}
