package com.huotu.huobanplus.search.service.impl;

import com.huotu.huobanplus.common.entity.GoodsKeywords;
import com.huotu.huobanplus.common.entity.MallTag;
import com.huotu.huobanplus.common.entity.support.SaleTags;
import com.huotu.huobanplus.sdk.common.repository.GoodsKeywordsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.view.*;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.service.GoodsService;
import com.huotu.huobanplus.search.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public ViewList search(Long customerId, Integer pageSize, Integer pageNo
            , String key, String brands, String category, String tags, Integer sorts) {
        Page<Goods> goodsPage = solrGoodsRepository.search(customerId, pageSize, pageNo, key, brands, category, tags, sorts);

        ViewList viewGoodsList = new ViewList();
        viewGoodsList.setPaging(new Paging(pageSize, pageNo, goodsPage.getTotalElements()));
        Long[] ids = new Long[goodsPage.getNumberOfElements()];
        for (int i = 0; i < goodsPage.getNumberOfElements(); i++) {
            ids[i] = goodsPage.getContent().get(i).getId();
        }
        viewGoodsList.setIds(ids);

        return viewGoodsList;
    }

    public void update(Long id) throws IOException {
        com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(id);
        update(mallGoods);
    }

    @Override
    public void update(Long merchantId, Long goodsId) throws IOException {
        List<com.huotu.huobanplus.common.entity.Goods> mallGoods = goodsRestRepository.searchMarketableByGoodsIds(merchantId, String.valueOf(goodsId));
        if (mallGoods != null) {
            update(mallGoods);
        }
    }

    public void update(com.huotu.huobanplus.common.entity.Goods goods) throws IOException {
        updateSolrGoods(goods);
    }

    @Override
    public void updateByCustomerId(Long merchantId) throws IOException {
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        for (; ; pageNo++) {
            Page<com.huotu.huobanplus.common.entity.Goods> mallGoodsPage = goodsRestRepository.searchByMerchantPK(merchantId, new PageRequest(pageNo, pageSize));
            if (mallGoodsPage.getNumberOfElements() == 0) {
                break;
            }
            update(mallGoodsPage.getContent());
        }
    }

    private void updateSolrGoods(com.huotu.huobanplus.common.entity.Goods mallGoods) throws IOException {
        Goods goods = getSolrGoodsFromMallGoods(mallGoods);
        if (goods != null) {
            solrGoodsRepository.save(goods);
        }
    }

    public void update(List<com.huotu.huobanplus.common.entity.Goods> mallGoods) throws IOException {
        List<Goods> goodsList = new ArrayList<>();
        for (com.huotu.huobanplus.common.entity.Goods goods : mallGoods) {
            if (goods != null) goodsList.add(getSolrGoodsFromMallGoods(goods));
        }
        if (goodsList.size() > 0)
            solrGoodsRepository.save(goodsList);
    }

    private Goods getSolrGoodsFromMallGoods(com.huotu.huobanplus.common.entity.Goods mallGoods) throws IOException {
        if (mallGoods != null) {
            Goods goods = solrGoodsRepository.findOne(mallGoods.getId());
            return mallGoodsToSolrGoods(mallGoods, goods);
        }
        return null;
    }


    public Goods mallGoodsToSolrGoods(com.huotu.huobanplus.common.entity.Goods mallGoods, Goods goods) throws IOException {
        if (mallGoods == null) {
            return null;
        }
        if (goods == null) {
            goods = new Goods();
            goods.setId(mallGoods.getId());
        }
        if (mallGoods.getOwner() != null) {
            goods.setCustomerId(mallGoods.getOwner().getId());
        }
        goods.setTitle(mallGoods.getTitle());
        if (mallGoods.getBrand() != null) {
            goods.setBrandId(mallGoods.getBrand().getId());
            goods.setBrandName(mallGoods.getBrand().getBrandName());
        }
        if (mallGoods.getCategory() != null) {
            goods.setCategoriesId(mallGoods.getCategory().getCatPath());
            // TODO: 2017-02-24 这里就不获取上级分类了，有需要了再作修改
            goods.setCategoryName(mallGoods.getCategory().getTitle());
        }
        goods.setDescription(mallGoods.getDescription());
        SaleTags saleTags;
        if (mallGoods.isUseCustomSaleTags()) {
            saleTags = mallGoods.getCustomSaleTags();
        } else {
            saleTags = mallGoods.getSaleTags();
        }
        if (saleTags != null) {
            goods.setHotspot(StringUtils.arrayToDelimitedString(saleTags.getTags(), "|"));
        }

        List<GoodsKeywords> mallGoodsKeywords = goodsKeywordsRestRepository.findAllByGoodsId(mallGoods.getId());
        if (mallGoodsKeywords != null) {
            StringBuilder keyword = new StringBuilder("|");
            for (GoodsKeywords goodsKeywords : mallGoodsKeywords) {
                if(goodsKeywords.getPk() != null && !StringUtils.isEmpty(goodsKeywords.getPk().getKeyword())){
                    keyword.append(goodsKeywords.getPk().getKeyword()).append("|");
                }
            }
            goods.setKeyword(keyword.toString());
        }

        Set<MallTag> mallTags = mallGoods.getTags();
        if (mallTags != null && mallTags.size() > 0) {
            StringBuilder tags = new StringBuilder("|");
            StringBuilder tagIds = new StringBuilder("|");
            for (MallTag mallTag : mallTags) {
                tags.append(mallTag.getTagName()).append("|");
                tagIds.append(mallTag.getId()).append("|");
            }
            goods.setTags(tags.toString());
            goods.setTagIds(tagIds.toString());
        }
        goods.setUpdateTime(mallGoods.getAutoMarketDate());
        goods.setSales((long) mallGoods.getSalesCount());
        goods.setOriginalPrice((float) mallGoods.getPrice());
        return goods;
    }
}
