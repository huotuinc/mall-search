package com.huotu.huobanplus.search;

import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.view.ViewList;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.service.GoodsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by helloztt on 2017-02-27.
 */
public class GoodsTestByZtt extends BaseTest {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SolrGoodsRepository solrGoodsRepository;

    private String searchKeyWord;
    private Goods goodsWithTitle, goodsWithKeyWord, goodsWithBrandName, goodsWithCategoryName,
            goodsWithVirtualCategory, goodsWithDescription, goodsWithTags, goodsWithHotspot;
    private Goods goodsWithCatId, goodsWithBrandId, goodsWithTagsId;
    private List<Goods> goodsList = new ArrayList<>();
    private int searchColumnNum = 0;

    @Before
    public void init() {
        searchKeyWord = "火图";
        goodsWithTitle = mockGoodsWithRandomId();
        goodsWithTitle.setTitle(searchKeyWord);
        searchColumnNum++;
        goodsList.add(goodsWithTitle);

        goodsWithKeyWord = mockGoodsWithRandomId();
        goodsWithKeyWord.setKeyword(searchKeyWord);
        searchColumnNum++;
        goodsList.add(goodsWithKeyWord);

        goodsWithBrandName = mockGoodsWithRandomId();
        goodsWithBrandName.setBrandName(searchKeyWord);
        searchColumnNum++;
        goodsList.add(goodsWithBrandName);

        goodsWithCategoryName = mockGoodsWithRandomId();
        goodsWithCategoryName.setCategoryName(searchKeyWord);
        searchColumnNum++;
        goodsList.add(goodsWithCategoryName);

        goodsWithVirtualCategory = mockGoodsWithRandomId();
        goodsWithVirtualCategory.setVirtualCategory(searchKeyWord);
        searchColumnNum++;
        goodsList.add(goodsWithVirtualCategory);

        goodsWithTags = mockGoodsWithRandomId();
        goodsWithTags.setTags(searchKeyWord);
        searchColumnNum++;
        goodsList.add(goodsWithTags);

        goodsWithDescription = mockGoodsWithRandomId();
        goodsWithDescription.setDescription(searchKeyWord);
        searchColumnNum++;
        goodsList.add(goodsWithDescription);

        goodsWithHotspot = mockGoodsWithRandomId();
        goodsWithHotspot.setHotspot(searchKeyWord);
        searchColumnNum++;
        goodsList.add(goodsWithHotspot);

        goodsWithCatId = mockGoodsWithRandomId();
        goodsWithCatId.setCategoriesId("|1|2|3|4|5|");
        goodsWithBrandId = mockGoodsWithRandomId();
        goodsWithBrandId.setBrandId(6L);
        goodsWithTagsId = mockGoodsWithRandomId();
        goodsWithTagsId.setTagIds("|9|8|7|");

        goodsList.add(goodsWithCatId);
        goodsList.add(goodsWithBrandId);
        goodsList.add(goodsWithTagsId);

        solrGoodsRepository.save(goodsList);
    }

    @After
    public void clearAll() {
        solrGoodsRepository.delete(goodsWithTitle);
        solrGoodsRepository.delete(goodsWithKeyWord);
        solrGoodsRepository.delete(goodsWithBrandName);
        solrGoodsRepository.delete(goodsWithCategoryName);
        solrGoodsRepository.delete(goodsWithVirtualCategory);
        solrGoodsRepository.delete(goodsWithDescription);
        solrGoodsRepository.delete(goodsWithTags);
        solrGoodsRepository.delete(goodsWithHotspot);
        solrGoodsRepository.delete(goodsWithCatId);
        solrGoodsRepository.delete(goodsWithBrandId);
        solrGoodsRepository.delete(goodsWithTagsId);
    }

    @Test
    public void searchBySearchKey() {

        //模糊搜索，权重从高到底依次为 商品名称，关键字，品牌，分类名称，虚拟分类名称，副标题，热点名称。
        ViewList resultList = goodsService.search(customerId, 10, 0, searchKeyWord, null, null, null, 0);
        assertNotNull(resultList);
        assertEquals(searchColumnNum, resultList.getIds().length);
        assertEquals(goodsWithTitle.getId(), resultList.getIds()[0]);
        assertEquals(goodsWithKeyWord.getId(), resultList.getIds()[1]);
        assertEquals(goodsWithBrandName.getId(), resultList.getIds()[2]);
        assertEquals(goodsWithCategoryName.getId(), resultList.getIds()[3]);
        assertEquals(goodsWithVirtualCategory.getId(), resultList.getIds()[4]);
        assertEquals(goodsWithTags.getId(), resultList.getIds()[5]);
        assertEquals(goodsWithDescription.getId(), resultList.getIds()[6]);
        assertEquals(goodsWithHotspot.getId(), resultList.getIds()[7]);

        goodsWithKeyWord.setBrandName(searchKeyWord);
        solrGoodsRepository.save(goodsWithKeyWord);
        resultList = goodsService.search(customerId, 10, 0, searchKeyWord, null, null, null, 0);
        assertNotNull(resultList);
        assertEquals(searchColumnNum, resultList.getIds().length);
        assertEquals(goodsWithKeyWord.getId(), resultList.getIds()[0]);
        assertEquals(goodsWithTitle.getId(), resultList.getIds()[1]);
    }

    @Test
    public void searchByIds() {
        //下拉筛选（商品分类，品牌，热点），多选
        String searchCatId = "3|5";
        String searchBrandId = "1|2|3|4|5|6";
        String searchTagsId = "10|9|7";

        ViewList resultList = goodsService.search(customerId, 10, 0, null, searchBrandId, null, null, null);
        assertNotNull(resultList);
        assertEquals(1, resultList.getIds().length);
        assertEquals(goodsWithBrandId.getId(), resultList.getIds()[0]);

        resultList = goodsService.search(customerId,10,0,null,null,searchCatId,null,null);
        assertNotNull(resultList);
        assertEquals(1, resultList.getIds().length);
        assertEquals(goodsWithCatId.getId(), resultList.getIds()[0]);

        resultList = goodsService.search(customerId,10,0,null,null,null,searchTagsId,null);
        assertNotNull(resultList);
        assertEquals(1, resultList.getIds().length);
        assertEquals(goodsWithTagsId.getId(), resultList.getIds()[0]);
    }

    @Test
    public void testBySort(){
        //按上架时间升序
        goodsList.sort(Comparator.comparing(Goods::getUpdateTime));
        ViewList resultList = goodsService.search(customerId, 10, 0, null, null, null, null, 10);
        for(int i = 0 ; i < resultList.getIds().length ; i++ ){
            assertEquals(goodsList.get(i).getId(),resultList.getIds()[i]);
        }

        //按上架时间降序
        goodsList.sort((Goods g1,Goods g2)->g2.getUpdateTime().compareTo(g1.getUpdateTime()));
        resultList = goodsService.search(customerId, 10, 0, null, null, null, null, 11);
        for(int i = 0 ; i < resultList.getIds().length ; i++ ){
            assertEquals(goodsList.get(i).getId(),resultList.getIds()[i]);
        }

        //按销量降序
        goodsList.sort((Goods g1,Goods g2)->g2.getSales().compareTo(g1.getSales()));
        resultList = goodsService.search(customerId, 10, 0, null, null, null, null, 21);
        for(int i = 0 ; i < resultList.getIds().length ; i++ ){
            assertEquals(goodsList.get(i).getId(),resultList.getIds()[i]);
        }

        //按价格升序
        goodsList.sort(Comparator.comparing(Goods::getOriginalPrice));
        resultList = goodsService.search(customerId, 10, 0, null, null, null, null, 30);
        for(int i = 0 ; i < resultList.getIds().length ; i++ ){
            assertEquals(goodsList.get(i).getId(),resultList.getIds()[i]);
        }

        //按价格降序
        goodsList.sort((Goods g1,Goods g2)->g2.getOriginalPrice().compareTo(g1.getOriginalPrice()));
        resultList = goodsService.search(customerId, 10, 0, null, null, null, null, 31);
        for(int i = 0 ; i < resultList.getIds().length ; i++ ){
            assertEquals(goodsList.get(i).getId(),resultList.getIds()[i]);
        }

    }


}
