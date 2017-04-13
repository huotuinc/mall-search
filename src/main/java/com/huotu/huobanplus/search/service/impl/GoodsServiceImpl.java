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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log log = LogFactory.getLog(GoodsServiceImpl.class);

    @Autowired
    private SolrGoodsRepository solrGoodsRepository;
    @Autowired
    private GoodsRestRepository goodsRestRepository;
    @Autowired
    private GoodsKeywordsRestRepository goodsKeywordsRestRepository;

    @Override
    public ViewList searchIds(Long customerId, Long supplierId, Long ownId, Integer pageSize, Integer pageNo
            , String key, String brands, String category, String typeIds, String tags, String sorts) {
        Page<Goods> goodsPage = solrGoodsRepository.search(customerId, supplierId, ownId, pageSize, pageNo, key, brands, category, typeIds, tags, sorts);

        ViewList viewGoodsList = new ViewList();
        viewGoodsList.setPageSize(pageSize);
        viewGoodsList.setPage(pageNo);
        viewGoodsList.setRecordCount(goodsPage.getTotalElements());
        viewGoodsList.setPageCount(goodsPage.getTotalPages());
//        viewGoodsList.setPaging(new Paging(pageSize, pageNo, goodsPage.getTotalElements()));
        Long[] ids = new Long[goodsPage.getNumberOfElements()];
        for (int i = 0; i < goodsPage.getNumberOfElements(); i++) {
            ids[i] = goodsPage.getContent().get(i).getId();
        }
        viewGoodsList.setIds(ids);

        return viewGoodsList;
    }

    @Override
    public ViewList search(Long customerId, Long supplierId, Long ownId, Integer pageSize, Integer pageNo
            , String key, String brands, String category, String typeIds, String tags, String sorts) {
        Page<Goods> goodsPage = solrGoodsRepository.search(customerId, supplierId, ownId, pageSize, pageNo, key, brands, category, typeIds, tags, sorts);

        ViewList viewGoodsList = new ViewList();
        viewGoodsList.setPageSize(pageSize);
        viewGoodsList.setPage(pageNo);
        viewGoodsList.setRecordCount(goodsPage.getTotalElements());
        viewGoodsList.setList(goodsPage.getContent());
        viewGoodsList.setPageCount(goodsPage.getTotalPages());
        return viewGoodsList;
    }

    @Override
    public Long maxId() throws IOException {
        return solrGoodsRepository.searchMaxId();
    }

    public void update(Long id) throws IOException {
        Goods solrGoods = solrGoodsRepository.findOne(id);
        solrGoods = mallGoodsToSolrGoods(id,solrGoods);
        if(solrGoods != null){
            solrGoodsRepository.save(solrGoods);
        }
    }

    @Override
    public void updateByCustomerId(Long merchantId) {
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        long goodsId = 0L;
        while (true){
            try {
                long start = System.currentTimeMillis();
                //如果按照分页一页一页查询，越后面的页码会越查越慢，所以设置起始的userId，每次只查第一页，这样会提高查询效率
                Page<com.huotu.huobanplus.common.entity.Goods> mallGoodsPage = goodsRestRepository.search(goodsId,merchantId,new PageRequest(pageNo,pageSize));
                long end = System.currentTimeMillis();
                if (mallGoodsPage.getNumberOfElements() == 0) {
                    break;
                }
                update(mallGoodsPage.getContent());
                long update = System.currentTimeMillis();
                Thread.sleep(10);
                log.debug("search page last " + (end - start) + " ms," +
                        "update data last " + (update - end) + " ms");
                goodsId = mallGoodsPage.getContent().get(mallGoodsPage.getNumberOfElements() - 1).getId();
            } catch (InterruptedException e) {
                log.error("sleep error");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            log.debug("sync goods start id " + goodsId + ", pageNo:" + pageNo + " success");
        }
    }

    /*private void updateSolrGoods(com.huotu.huobanplus.common.entity.Goods mallGoods) throws IOException {
        Goods goods = getSolrGoodsFromMallGoods(mallGoods);
        if (goods != null) {
            solrGoodsRepository.save(goods);
        }
    }*/

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

    private Goods mallGoodsToSolrGoods(com.huotu.huobanplus.common.entity.Goods mallGoods, Goods solrGoods) throws IOException{
        if(mallGoods == null){
            return null;
        }
        if(solrGoods == null){
            solrGoods = new Goods();
            solrGoods.setId(mallGoods.getId());
        }
        solrGoods.setCustomerId(mallGoods.getSolrGoods().getCustomerId());
        solrGoods.setOwnerId(mallGoods.getSolrGoods().getOwnerId());
        solrGoods.setTitle(mallGoods.getSolrGoods().getTitle());
        solrGoods.setKeyword(mallGoods.getSolrGoods().getKeyword());
        solrGoods.setBrandName(mallGoods.getSolrGoods().getBrandName());
        solrGoods.setCategoryName(mallGoods.getSolrGoods().getCategoryName());
        solrGoods.setTags(mallGoods.getSolrGoods().getTags());
        solrGoods.setDescription(mallGoods.getSolrGoods().getDescription());
        solrGoods.setHotspot(mallGoods.getSolrGoods().getHotspot());
        solrGoods.setBrandId(mallGoods.getSolrGoods().getBrandId());
        solrGoods.setCategoriesId(mallGoods.getSolrGoods().getCategoriesId());
        solrGoods.setTagIds(mallGoods.getSolrGoods().getTagIds());
        solrGoods.setUpdateTime(mallGoods.getSolrGoods().getUpdateTime());
        solrGoods.setSales(mallGoods.getSolrGoods().getSales());
        solrGoods.setDisabled(mallGoods.getSolrGoods().isDisabled());
        solrGoods.setTypeId(mallGoods.getSolrGoods().getTypeId());
        solrGoods.setCatId(mallGoods.getSolrGoods().getCategoryId());
        solrGoods.setSupplierId(mallGoods.getSolrGoods().getSupplierId());
        if(mallGoods.getSolrGoods().getSmallPic() != null){
            solrGoods.setSmallPic(mallGoods.getSolrGoods().getSmallPic().getValue());
        }
        solrGoods.setBrief(mallGoods.getSolrGoods().getBrief());
        solrGoods.setMarketPrice(mallGoods.getSolrGoods().getMarketPrice());
        solrGoods.setPrice(mallGoods.getSolrGoods().getPrice());
        solrGoods.setSpec(mallGoods.getSolrGoods().getSpec());
        solrGoods.setPdtDesc(mallGoods.getSolrGoods().getPdtDesc());
        solrGoods.setSpecDesc(mallGoods.getSolrGoods().getSpecDesc());
        solrGoods.setVipRebateType(mallGoods.getSolrGoods().getVipRebateType());
        solrGoods.setVipRebateItems(mallGoods.getSolrGoods().getVipRebateItems());
        solrGoods.setPriceLevelDesc(mallGoods.getSolrGoods().getPriceLevelDesc());
        solrGoods.setDisRebateDesc(mallGoods.getSolrGoods().getDisRebateDesc());
        solrGoods.setCustomTmrRebateSetting(mallGoods.getSolrGoods().getCustomTmrRebateSetting());
        solrGoods.setIndividuation(mallGoods.getSolrGoods().getIndividuation() != null && mallGoods.getSolrGoods().getIndividuation() == true ? 1 : 0);
        solrGoods.setRebateMode(mallGoods.getSolrGoods().getRebateMode());
        solrGoods.setRebateQuotaSetting(mallGoods.getSolrGoods().getRebateQuotaSetting());
        solrGoods.setRebateQuotaRatio(mallGoods.getSolrGoods().getRebateQuotaRatio());
        solrGoods.setRebateSaleSetting(mallGoods.getSolrGoods().getRebateSaleSetting());
        solrGoods.setRebateSaleRatio(mallGoods.getSolrGoods().getRebateSaleRatio());
        solrGoods.setCustomManagerRebateFlag(mallGoods.getSolrGoods().getCustomManagerRebateFlag());
        return solrGoods;
    }

    private Goods mallGoodsToSolrGoods(Long goodsId, Goods goods) throws IOException {
        com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(goodsId);
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
        if (mallGoods.getSaleShopId() != null) {
            goods.setOwnerId(mallGoods.getSaleShopId());
        } else {
            goods.setOwnerId(0L);
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
                if (goodsKeywords.getPk() != null && !StringUtils.isEmpty(goodsKeywords.getPk().getKeyword())) {
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
        goods.setDisabled(mallGoods.isDisabled());
        return goods;
    }
}
