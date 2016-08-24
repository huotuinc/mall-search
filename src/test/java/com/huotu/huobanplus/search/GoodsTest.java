package com.huotu.huobanplus.search;

import com.huotu.huobanplus.search.boot.BootConfig;
import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.view.ViewGoodsList;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.repository.solr.SolrHotRepository;
import com.huotu.huobanplus.search.service.GoodsService;
import com.huotu.huobanplus.search.service.HotService;
import com.huotu.huobanplus.search.utils.PinyinUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * Created by Administrator on 2016/8/18.
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {BootConfig.class})
@ActiveProfiles("test")
public class GoodsTest extends BaseTest {


    @Autowired
    private SolrGoodsRepository solrGoodsRepository;

    @Autowired
    private GoodsService goodsService;


    @Autowired
    private SolrHotRepository solrHotRepository;

    @Autowired
    private HotService hotService;


    @Before
    public void init() {
        customerId = 3447L;
    }

    @After
    public void clearAll() {
//        solrGoodsRepository.deleteAll();
//        solrHotRepository.deleteAll();
    }


    @Test
    public void searchGoods() {
        Goods goods = createGoods(1L, "化妆品", "");
        solrGoodsRepository.save(goods);
        Goods goods1 = createGoods(2L, "", "化妆品");
        solrGoodsRepository.save(goods1);

        //在没有搜索内容的情况下，应当为空
        ViewGoodsList viewGoodsList = goodsService.search(customerId, 10, 0, 0, "电子", 0, 0, "", 0);
        Assert.assertEquals(0, viewGoodsList.getList().size());

        //在有搜索的情况下，根据权重情况进行先后处理(完全匹配)
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品", 0, 0, "", 0);
        Assert.assertEquals(2, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());

        //完全匹配跟权重的比较 完全匹配靠前
        goods.setTitle("我有 化妆品的en公司");
        solrGoodsRepository.save(goods);
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品", 0, 0, "", 0);
        Assert.assertEquals(2, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());

        //在有搜索的情况下，根据权重情况进行先后处理(非完全匹配)
        goods1.setDescription("我有 化妆品的en作坊");
        solrGoodsRepository.save(goods1);
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品", 0, 0, "", 0);
        Assert.assertEquals(2, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());

        //复杂词的搜索 不能带特殊字符如空格
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品 公司", 0, 0, "", 0);
        Assert.assertEquals(0, viewGoodsList.getList().size());

        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "弟弟化妆品的信息", 0, 0, "", 0);
        Assert.assertEquals(2, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());

        //简单组合词
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆", 0, 0, "", 0);
        Assert.assertEquals(0, viewGoodsList.getList().size());
//        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());


        //非搜索词
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "有化", 0, 0, "", 0);
        Assert.assertEquals(0, viewGoodsList.getList().size());

        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化品", 0, 0, "", 0);
        Assert.assertEquals(0, viewGoodsList.getList().size());

        //单词
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "品", 0, 0, "", 0);
        Assert.assertEquals(0, viewGoodsList.getList().size());

        goods1.setTitle("化妆品");
        solrGoodsRepository.save(goods1);
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品", 0, 0, "", 0);
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getList().get(0).getId());
    }

    @Test
    public void searchGoodsSort() {
        Goods goods = createGoods(1L, "我有化妆品的en公司", "");
        goods.setSales(100L);
        solrGoodsRepository.save(goods);
        Goods goods1 = createGoods(2L, "", "他有化妆品的en作坊");
        goods1.setSales(200L);
        solrGoodsRepository.save(goods1);
        //在找到匹配的情况下 按照指定销量倒序优先
        ViewGoodsList viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品", 0, 0, "", 2);
        Assert.assertEquals(2, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getList().get(0).getId());

        //匹配度高的情况下 优先排序
        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "我有化妆品的en公司", 0, 0, "", 2);
        Assert.assertEquals(2, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getList().get(0).getId());

    }

    @Test
    public void searchGoodsFilter() {
        Goods goods = createGoods(1L, "化妆品", "");
        goods.setCategoryId(100);
        goods.setBrandsId(200);
        goods.setHotspot("海南直发");
        solrGoodsRepository.save(goods);

        Goods goods1 = createGoods(2L, "化妆品", "");
        goods1.setCategoryId(101);
        goods1.setBrandsId(201);
        goods1.setHotspot("特价商品 中国制造");
        solrGoodsRepository.save(goods1);

        ViewGoodsList viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品", 0, 101, "", 2);
        Assert.assertEquals(1, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getList().get(0).getId());

        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品", 201, 0, "", 2);
        Assert.assertEquals(1, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getList().get(0).getId());


        viewGoodsList = goodsService.search(customerId, 10, 0, 0, "化妆品", 0, 0, "中国制造", 2);
        Assert.assertEquals(1, viewGoodsList.getList().size());
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getList().get(0).getId());


    }

    @Test
    public void suggest() {

        hotService.save(customerId, "科技公司");
        hotService.save(customerId, "科技公司");
        hotService.save(customerId, "阿里公司老大");
        hotService.save(customerId, "公司老大");
        hotService.save(customerId, "科技发展");
        hotService.save(customerId, "科技发展");
        hotService.save(customerId, "科技发展");
        hotService.save(customerId, "科技中");

        List<String> list = hotService.suggest(customerId, "科技", 10);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("科技发展", list.get(0).toString());
        Assert.assertEquals("科技公司", list.get(1).toString());
        Assert.assertEquals("科技中", list.get(2).toString());

        list = hotService.suggest(customerId, "keji", 10);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("科技发展", list.get(0).toString());
        Assert.assertEquals("科技公司", list.get(1).toString());
        Assert.assertEquals("科技中", list.get(2).toString());


    }

    @Test
    public void testPinyin() {
        System.out.println(PinyinUtils.getFullSpell("科技"));

    }

    @Test
    public void createGoods() {
        //创建10万数据
//        List<Goods> goodses = createGoodsList(1000);
//        solrGoodsRepository.save(goodses);
////        Assert.assertEquals(goodses.size(), solrGoodsRepository.count());
//        System.out.println(solrGoodsRepository.count());
    }


    @Test
    public void createGoodsProduct() {
//        List<Goods> goodses = createGoodsList(1000);
//        solrGoodsRepository.save(goodses);
////        Assert.assertEquals(goodses.size(), solrGoodsRepository.count());
//        System.out.println(solrGoodsRepository.count());
//
//        List<Product> products = createProductList(500);
//        solrProductRepository.save(products);
//        System.out.println(solrProductRepository.count());
    }

    @Test
    public void partUpdate() {
//        //对其中一条进行局部更新
//        String updateTitle = "更新的标题";
//        Goods goods = solrGoodsRepository.findOne(20l);
//        goods.setTitle(updateTitle);
//        solrGoodsRepository.save(goods);
//
//        Goods find = solrGoodsRepository.findOne(20l);
//        Assert.assertEquals(updateTitle, find.getTitle());
    }
}
