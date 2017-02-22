package com.huotu.huobanplus.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.huotu.huobanplus.common.entity.GoodsKeywords;
import com.huotu.huobanplus.common.entity.MallTag;
import com.huotu.huobanplus.common.entity.support.SaleTags;
import com.huotu.huobanplus.sdk.common.repository.GoodsKeywordsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.view.Paging;
import com.huotu.huobanplus.search.model.view.ViewGoods;
import com.huotu.huobanplus.search.model.view.ViewGoodsList;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lgh on 2016/8/1.
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SolrGoodsRepository solrGoodsRepository;

    @Autowired
    private GoodsRestRepository goodsRestRepository;

    @Autowired
    private GoodsKeywordsRestRepository goodsKeywordsRestRepository;

    @Override
    public ViewGoodsList search(Long customerId, Integer pageSize, Integer page, Integer levelId
            , String key, String brands, String category, String hotspot, Integer sorts) {
        //todo  think levelId
        //data check
        if (customerId == null) throw new IllegalArgumentException();
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (page == null || page <= 0) page = 0;

        Page<Goods> goodses = solrGoodsRepository.search(customerId, pageSize, page, levelId, key, brands, category, hotspot, sorts);

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
            viewGoods.setPriceDesc(goods.getPriceDesc());
//            viewGoods.setSummary(goods.);
            viewGoodses.add(viewGoods);
        }
        viewGoodsList.setList(viewGoodses);

        return viewGoodsList;
    }

    public void update(com.huotu.huobanplus.common.entity.Goods goods) throws IOException {
        updateSolrGoods(goods);
    }

    public void update(Long id) throws IOException {
        com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(id);
        update(mallGoods);
    }

    private void updateSolrGoods(com.huotu.huobanplus.common.entity.Goods mallGoods) throws IOException {
        Goods goods = getSolrGoods(mallGoods);
        if (goods != null) solrGoodsRepository.save(goods);
    }

    public void update(List<com.huotu.huobanplus.common.entity.Goods> mallGoods) throws IOException {
        List<Goods> goodsList = new ArrayList<>();
        for (com.huotu.huobanplus.common.entity.Goods goods : mallGoods) {
            if (goods != null) goodsList.add(getSolrGoods(goods));
        }
        if (goodsList.size() > 0)
            solrGoodsRepository.save(goodsList);
    }

    private Goods getSolrGoods(com.huotu.huobanplus.common.entity.Goods mallGoods) throws IOException {
        if (mallGoods != null) {
            Goods goods = solrGoodsRepository.findOne(mallGoods.getId());
            if (goods == null) {
                goods = new Goods();
                goods = mallGoodsToSolrGoods(mallGoods, goods);
            } else {
                goods = mallGoodsToSolrGoods(mallGoods, goods);
            }
            return goods;
        }
        return null;
    }


    public Goods mallGoodsToSolrGoods(com.huotu.huobanplus.common.entity.Goods mallGoods, Goods goods) throws IOException {
        goods.setId(mallGoods.getId());
        if (mallGoods.getOwner() != null) goods.setCustomerId(mallGoods.getOwner().getId());

        goods.setTitle(mallGoods.getTitle());
        goods.setPrice((float) mallGoods.getPrice());
        goods.setOriginalPrice((float) mallGoods.getMarketPrice());
        goods.setMemberPrice((float) mallGoods.getPrice());//todo

        if (mallGoods.getImages().size() > 0)
            goods.setPictureUrl(mallGoods.getImages().get(0).getSmallPic().getValue());

//        goods.setDescription(mallGoods.getDescription());//todo

        List<GoodsKeywords> mallGoodsKeywords = goodsKeywordsRestRepository.findAllByGoodsId(mallGoods.getId());
        StringBuilder keyword = new StringBuilder();
        if (mallGoodsKeywords != null) {
            for (GoodsKeywords goodsKeywords : mallGoodsKeywords) {
                if (org.springframework.util.StringUtils.isEmpty(keyword.toString()))
                    keyword.append(goodsKeywords.getPk().getKeyword());
                else
                    keyword.append("|".concat(goodsKeywords.getPk().getKeyword()));
            }
        }
        goods.setKeyword(keyword.toString());

//        if (mallGoods.getSupplier() != null)
//            goods.setSupplier(mallGoods.getSupplier().getName());

//        StringBuilder tags = new StringBuilder();
//        Set<MallTag> mallTags = mallGoods.getTags();
//        if (mallTags != null) {
//            for (MallTag mallTag : mallTags) {
//                if (org.springframework.util.StringUtils.isEmpty(tags.toString()))
//                    tags.append(mallTag.getTagName());
//                else
//                    tags.append("|".concat(mallTag.getTagName()));
//            }
//        }
//        goods.setTags(tags.toString());

        if (mallGoods.getBrand() != null) goods.setBrandsId(mallGoods.getBrand().getId());
        if (mallGoods.getCategory() != null) goods.setCategoryId(mallGoods.getCategory().getId());

        if (mallGoods.isUseCustomSaleTags()) {
            SaleTags saleTags = mallGoods.getCustomSaleTags();
            if (saleTags != null)
                goods.setHotspot(StringUtils.join(saleTags.getTags(), "|"));
        } else {
            SaleTags saleTags = mallGoods.getSaleTags();
            if (saleTags != null)
                goods.setHotspot(StringUtils.join(saleTags.getTags(), "|"));
        }

        goods.setUpdateTime(mallGoods.getAutoMarketDate());
        goods.setSales((long) mallGoods.getSalesCount());

        goods.setPriceDesc(JSON.toJSONString(mallGoods.getPricesCache()));
        goods.setRebateDesc(JSON.toJSONString(mallGoods.getRebateConfiguration()));
        return goods;
    }
}
