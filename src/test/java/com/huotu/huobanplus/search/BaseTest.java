package com.huotu.huobanplus.search;

import com.huotu.huobanplus.search.boot.BootConfig;
import com.huotu.huobanplus.search.boot.MvcConfig;
import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.solr.User;
import org.junit.runner.RunWith;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Administrator on 2016/8/12.
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {BootConfig.class, MvcConfig.class})
@ActiveProfiles("test")
public abstract class BaseTest extends SpringWebTest{

    private Random random = new Random();
    protected Long customerId = Long.valueOf(random.nextInt(100));

    
    protected List<Goods> mockGoodsList(int count) {
        List<Goods> goodses = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            goodses.add(mockGoodsById(i));
        }
        return goodses;
    }

    protected Goods mockGoodsWithRandomId(){
        Goods goods = new Goods();
        goods.setId(Long.valueOf(Math.abs(random.nextInt())));
        goods.setCustomerId(customerId);
        goods.setDisabled(false);
        goods.setOwnerId(-1L);
        goods.setUpdateTime(Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(LocalDateTime.now().minusMinutes(random.nextInt(100))));
        goods.setSales(Math.abs(random.nextLong()));
        goods.setOriginalPrice(Math.abs(random.nextFloat()));
        return goods;
    }

    protected Goods mockGoodsById(Long id) {
        return mockGoodsByIdAndSales(id, 10 * id + 1);
    }

    protected Goods mockGoodsByIdAndSales(Long id, Long sales) {
        Goods goods = new Goods();
        goods.setId(id);
        goods.setCustomerId(customerId);
        goods.setDisabled(false);
        goods.setOwnerId(-1L);
        goods.setTitle("goods-" + id);
//        goods.setPrice(id * 10F);
        goods.setOriginalPrice(id * 20F);
//        goods.setMemberPrice(id * 5F);
//        goods.setPictureUrl("http://www.huobanplus.com/" + id + ".png");
        goods.setDescription("描述" + id);
        goods.setKeyword("关键字" + id);
//        goods.setSupplier("");
        goods.setTags("标签" + id);
//        goods.setVirturalCatetory("虚拟分类");
        goods.setBrandId(0L);
        goods.setCategoriesId("|0|");
        goods.setHotspot("免运费");
        goods.setUpdateTime(new Date());
        goods.setSales(sales);
//        goods.setPriceDesc("");
        return goods;
    }


    protected Goods mockGoodsByTitleAndDescription(Long id, String title, String description) {
        return mockGoods(id, title, description, "", "", "", "");
    }

    protected Goods mockGoods(Long id, String title, String description, String keyword, String supplier, String tags, String virturalCatetory) {
        Goods goods = new Goods();
        goods.setId(id);
        goods.setCustomerId(customerId);
        goods.setDisabled(false);
        goods.setOwnerId(-1L);
        goods.setTitle(title);
//        goods.setPrice(id * 10F);
        goods.setOriginalPrice(id * 20F);
//        goods.setMemberPrice(id * 5F);
//        goods.setPictureUrl("http://www.huobanplus.com/" + id + ".png");
        goods.setDescription(description);
        goods.setKeyword(keyword);
//        goods.setSupplier(supplier);
        goods.setTags(tags);
//        goods.setVirturalCatetory(virturalCatetory);
        goods.setBrandId(0L);
        goods.setCategoriesId("|0|");
        goods.setHotspot("免运费");
        goods.setUpdateTime(new Date());
        goods.setSales(1000L);
//        goods.setPriceDesc("");
        return goods;
    }

    protected User mockUser(){
        User user = new User();
        user.setId(Long.valueOf(Math.abs(random.nextInt())));
        user.setCustomerId(customerId);
        user.setLevelId(Math.abs(random.nextInt(10)));
        user.setUserType(random.nextInt(2));
        user.setLoginName(UUID.randomUUID().toString());
        user.setOpenId(UUID.randomUUID().toString());
        user.setParentLoginName(UUID.randomUUID().toString());
        user.setMobileBindRequired(false);
        user.setNickName(UUID.randomUUID().toString());
        user.setRealName(UUID.randomUUID().toString());
        user.setUserBalance(Math.abs(random.nextDouble()));
        user.setUserIntegral(Long.valueOf(Math.abs(random.nextInt(1000))));
        user.setRegTime(Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(LocalDateTime.now().minusMinutes(random.nextInt(100))));
        user.setDeleted(false);
        return user;
    }
}
