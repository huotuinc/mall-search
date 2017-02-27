package com.huotu.huobanplus.search;

import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.search.model.solr.Goods;
import com.huotu.huobanplus.search.model.view.ViewGoodsList;
import com.huotu.huobanplus.search.repository.solr.SolrGoodsRepository;
import com.huotu.huobanplus.search.service.GoodsService;
import com.huotu.huobanplus.search.service.HotService;
import com.huotu.huobanplus.search.utils.PinyinUtils;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.FacetPage;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/8/18.
 */


public class GoodsTest extends BaseTest {


    @Autowired
    private SolrGoodsRepository solrGoodsRepository;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private HotService hotService;
    @Autowired
    private SolrTemplate solrGoodsTemplate;

    private Goods goods,goods1;


    @Before
    public void init() {
        customerId = 3447L;
        goods = solrGoodsRepository.findOne(1L);
        if(goods == null){
            goods = mockGoodsByTitleAndDescription(1L, "化妆品", "");
        }else{
            goods.setTitle("化妆品");
            goods.setDescription("");
        }
        solrGoodsRepository.save(goods);
        goods1 = solrGoodsRepository.findOne(2L);
        if(goods1 == null){
            goods1 = mockGoodsByTitleAndDescription(2L, "", "化妆品");
        }else{
            goods1.setTitle("");
            goods1.setDescription("化妆品");
        }
        solrGoodsRepository.save(goods1);
    }

    @After
    public void clearAll() {
//        solrGoodsRepository.deleteAll();
//        solrHotRepository.deleteAll();
    }

    @Test
    public void removeAll(){
        solrGoodsRepository.deleteAll();
    }

    @Test
    public void facetQueryTest(){
        FacetQuery query = new SimpleFacetQuery(new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD))
                .setFacetOptions(new FacetOptions().addFacetOnField("title").setFacetLimit(5));
        FacetPage<Goods> goodsFacetPage = solrGoodsTemplate.queryForFacetPage(query,Goods.class);
        Assert.assertNotNull(goodsFacetPage);
        Assert.assertTrue(goodsFacetPage.getTotalElements() > 0);

        FacetQuery queryTitle = new SimpleFacetQuery(new SimpleStringCriteria("title:化妆品"));
        FacetPage<Goods> queryTitlePage = solrGoodsTemplate.queryForFacetPage(queryTitle,Goods.class);
        Assert.assertNotNull(goodsFacetPage);
        Assert.assertTrue(queryTitlePage.getTotalElements() > 0);

        FacetQuery queryTitle1 = new SimpleFacetQuery(new SimpleStringCriteria("title:*"));
        FacetPage<Goods> queryTitle1Page = solrGoodsTemplate.queryForFacetPage(queryTitle1,Goods.class);
        Assert.assertNotNull(queryTitle1Page);
        Assert.assertTrue(queryTitle1Page.getTotalElements() > 0);

        FacetQuery queryTitle2 = new SimpleFacetQuery(new SimpleStringCriteria("title:品妆化"));
        FacetPage<Goods> queryTitle2Page = solrGoodsTemplate.queryForFacetPage(queryTitle2,Goods.class);
        Assert.assertNotNull(queryTitle2Page);
        Assert.assertTrue(queryTitle2Page.getTotalElements() == 0);

        FacetQuery queryTitle3 = new SimpleFacetQuery(new SimpleStringCriteria("title:?品"));
        FacetPage<Goods> queryTitle3Page = solrGoodsTemplate.queryForFacetPage(queryTitle3,Goods.class);
        Assert.assertNotNull(queryTitle3Page);
        Assert.assertTrue(queryTitle3Page.getTotalElements() == 0);
    }


    @Test
    public void searchGoods() {
        //在没有搜索内容的情况下，应当为空
        ViewGoodsList viewGoodsList = goodsService.search(customerId, 10, 0,  "电子", "", "", "", null);
        Assert.assertEquals(0, viewGoodsList.getIds().length);

        //在有搜索的情况下，根据权重情况进行先后处理(完全匹配)
        viewGoodsList = goodsService.search(customerId, 10, 0,  "化妆品", "", "", "", null);
        Assert.assertEquals(2, viewGoodsList.getIds().length);
//        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());

        //完全匹配跟权重的比较 完全匹配靠前
        goods.setTitle("我有 化妆品的en公司");
        solrGoodsRepository.save(goods);
        viewGoodsList = goodsService.search(customerId, 10, 0,  "化妆品", "", "", "", null);
        Assert.assertEquals(2, viewGoodsList.getIds().length);
//        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());

        //在有搜索的情况下，根据权重情况进行先后处理(非完全匹配)
        goods1.setDescription("我有 化妆品的en作坊");
        solrGoodsRepository.save(goods1);
        viewGoodsList = goodsService.search(customerId, 10, 0,  "化妆品", "", "", "", null);
        Assert.assertEquals(2, viewGoodsList.getIds().length);
//        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());

        //复杂词的搜索 带特殊字符如空格
        viewGoodsList = goodsService.search(customerId, 10, 0,  "化妆品 公司", "", "", "", null);
        Assert.assertEquals(2, viewGoodsList.getIds().length);

        viewGoodsList = goodsService.search(customerId, 10, 0,  "弟弟化妆品的信息", "", "", "", null);
        Assert.assertEquals(2, viewGoodsList.getIds().length);
//        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());

        //简单组合词
        viewGoodsList = goodsService.search(customerId, 10, 0,  "化妆", "", "", "", null);
        Assert.assertEquals(0, viewGoodsList.getIds().length);
//        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getList().get(0).getId());


        //非搜索词
        viewGoodsList = goodsService.search(customerId, 10, 0,  "有化", "", "", "", null);
        Assert.assertEquals(0, viewGoodsList.getIds().length);

        viewGoodsList = goodsService.search(customerId, 10, 0,  "化品", "", "", "", null);
        Assert.assertEquals(0, viewGoodsList.getIds().length);

        //单词
        viewGoodsList = goodsService.search(customerId, 10, 0, "品", "", "", "", null);
        Assert.assertEquals(0, viewGoodsList.getIds().length);

        goods1.setTitle("化妆品");
        solrGoodsRepository.save(goods1);
        viewGoodsList = goodsService.search(customerId, 10, 0, "化妆品", "", "", "", null);
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getIds()[0]);
    }

    @Test
    public void searchGoodsSort() {
        goods.setTitle("中国化妆品的en公司");
        goods.setDescription("");
        goods.setSales(100L);
        solrGoodsRepository.save(goods);
        goods1.setTitle("");
        goods1.setDescription("美国化妆品的en作坊");
        goods1.setSales(200L);
        solrGoodsRepository.save(goods1);

        //在找到匹配的情况下 按照指定销量倒序优先
        ViewGoodsList viewGoodsList = goodsService.search(customerId, 10, 0,  "en", "", "", "", 21);
        Assert.assertEquals(2, viewGoodsList.getIds().length);
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getIds()[0]);

        //匹配度高的情况下 优先排序
        viewGoodsList = goodsService.search(customerId, 10, 0,  "中国化妆品 公司", "", "", "", 21);
        Assert.assertEquals(1, viewGoodsList.getIds().length);
        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getIds()[0]);

    }

    @Test
    public void searchGoodsWeight(){
        ViewGoodsList viewGoodsList = goodsService.search(customerId, 10, 0,  "化妆品", "", "", "", null);
        Assert.assertEquals(2, viewGoodsList.getIds().length);
        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getIds()[0]);

        solrGoodsRepository.delete(2L);
        solrGoodsRepository.delete(1L);
        solrGoodsRepository.save(goods1);
        solrGoodsRepository.save(goods);
        viewGoodsList = goodsService.search(customerId, 10, 0,  "化妆品", "", "", "", null);
        Assert.assertEquals(2, viewGoodsList.getIds().length);
        Assert.assertEquals(Long.valueOf(1), viewGoodsList.getIds()[0]);
    }

    @Test
    public void searchGoodsFilter() {
        goods.setCategoriesId("|1|");
        goods.setBrandId(1L);
        goods.setHotspot("中国");
        solrGoodsRepository.save(goods);

        goods1.setCategoriesId("|2|");
        goods1.setBrandId(2L);
        goods1.setTagIds("|4|5|6|");
        solrGoodsRepository.save(goods1);

        //key brands category hotspot
        ViewGoodsList viewGoodsList = goodsService.search(customerId, 10, 0, "化妆品", "", "2", "", 21);
        Assert.assertEquals(1, viewGoodsList.getIds().length);
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getIds()[0]);

        viewGoodsList = goodsService.search(customerId, 10, 0, "化妆品", "2", "", "", 21);
        Assert.assertEquals(1, viewGoodsList.getIds().length);
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getIds()[0]);


         viewGoodsList = goodsService.search(customerId, 10, 0, "化妆品", "", "", "6", 21);
        Assert.assertEquals(1, viewGoodsList.getIds().length);
        Assert.assertEquals(Long.valueOf(2), viewGoodsList.getIds()[0]);


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
        String[] x = {"a", "b", "c"};
        System.out.println(org.apache.commons.lang.StringUtils.join(x, "|"));

        Set<String> t = new HashSet<>();
        t.add("a");
        t.add("b");
        t.add("d");

        StringBuilder tags = new StringBuilder();
        for (String item : t) {
            tags.append(item.concat("|"));
        }
        System.out.println(tags.toString());

        System.out.println(org.apache.commons.lang.StringUtils.join(t, "|"));
    }

    @Test
    @Ignore
    public void testUpdate() throws IOException {
        goodsService.update(99567L);
    }

    @Autowired
    private GoodsRestRepository goodsRestRepository;

    @Test
    public void testImportGoods() throws IOException {
        Pageable pageable = new PageRequest(0, 10);
        Page<com.huotu.huobanplus.common.entity.Goods> goodsPage = goodsRestRepository.findAll(pageable);
        goodsService.update(goodsPage.getContent());
    }
}
