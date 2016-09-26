package com.huotu.huobanplus.search;

import com.huotu.huobanplus.search.model.solr.Goods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */


public abstract class BaseTest {

    protected Long customerId;
    
    protected List<Goods> createGoodsList(int count) {
        List<Goods> goodses = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            goodses.add(createGoods(i));
        }
        return goodses;
    }

    protected Goods createGoods(Long id) {
        return createGoods(id, 10 * id + 1);
    }

    protected Goods createGoods(Long id, Long sales) {
        Goods goods = new Goods();
        goods.setId(id);
        goods.setCustomerId(customerId);
        goods.setTitle("goods-" + id);
        goods.setPrice(id * 10F);
        goods.setOriginalPrice(id * 20F);
        goods.setMemberPrice(id * 5F);
        goods.setPictureUrl("http://www.huobanplus.com/" + id + ".png");
        goods.setDescription("描述" + id);
        goods.setKeyword("关键字" + id);
        goods.setSupplier("");
        goods.setTags("标签" + id);
//        goods.setVirturalCatetory("虚拟分类");
        goods.setBrandsId(0L);
        goods.setCategoryId(0L);
        goods.setHotspot("免运费");
        goods.setUpdateTime(new Date());
        goods.setSales(sales);
        goods.setPriceDesc("");
        return goods;
    }


    protected Goods createGoods(Long id, String title, String description) {
        return createGoods(id, title, description, "", "", "", "");
    }

    protected Goods createGoods(Long id, String title, String description, String keyword, String supplier, String tags, String virturalCatetory) {
        Goods goods = new Goods();
        goods.setId(id);
        goods.setCustomerId(customerId);
        goods.setTitle(title);
        goods.setPrice(id * 10F);
        goods.setOriginalPrice(id * 20F);
        goods.setMemberPrice(id * 5F);
        goods.setPictureUrl("http://www.huobanplus.com/" + id + ".png");
        goods.setDescription(description);
        goods.setKeyword(keyword);
        goods.setSupplier(supplier);
        goods.setTags(tags);
//        goods.setVirturalCatetory(virturalCatetory);
        goods.setBrandsId(0L);
        goods.setCategoryId(0L);
        goods.setHotspot("免运费");
        goods.setUpdateTime(new Date());
        goods.setSales(1000L);
        goods.setPriceDesc("");
        return goods;
    }
}
