package com.huotu.huobanplus.search.controller;

import com.huotu.huobanplus.search.model.view.ViewList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */

@RequestMapping("/goods")
public interface GoodsController {


    /***
     * 商品搜索，返回商品主键列表
     * 同时搜索词进入搜索热度
     * @param customerId 商家Id
     * @param ownId 所属店铺，默认-1
     * @param pageSize 每页尺寸 默认10
     * @param pageNo 当前页 默认0第一页
     * @param key 搜索关键字
     *            搜索范围：商品的标题，副标题，关键字，标签，供应商 可用空格分隔
     *            example：阿迪达斯T恤 equal to 阿迪达斯 T恤
     *            权重：按照 商品名称，关键字，品牌，分类名称，副标题，热点名称 顺序依次由高到低
     * @param  brands 品牌 （筛选项 品牌id |隔开）
     * @param category 分类 （筛选项 分类id |隔开）
     * @param typeIds 类目 筛选项 分类id |隔开）
     * @param  tags 商品标签（正品保证|海外直采|免税闪购|七天退换|海南直发）（筛选项 |隔开）
     * @param sorts 排序
     *              排序依据：新品，销量，价格
     *              example：
     *              按时间降序排序 = 1,
     *              按时间升序排序 = 2,
     *              按销售量降序排序 = 3,
     *              按销售量升序排序 = 4,
     *              按价格降序排序 = 5,
     *              按价格升序排序 = 6
     * @return 商品主键列表
     */
    @RequestMapping(value = "/searchIds", method = RequestMethod.POST)
    @ResponseBody
    ViewList searchIds(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "supplierId") Long supplierId
            , @RequestParam(value = "ownId", defaultValue = "-1") Long ownId
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo
            , @RequestParam(value = "key", required = false) String key
            , @RequestParam(value = "brands", required = false) String brands
            , @RequestParam(value = "category", required = false) String category
            , @RequestParam(value = "typeIds", required = false) String typeIds
            , @RequestParam(value = "tags", required = false) String tags
            , @RequestParam(value = "sorts", required = false) String sorts

    );

    /***
     * 商品搜索，返回商品主键列表
     * 同时搜索词进入搜索热度
     * @param customerId 商家Id
     * @param supplierId  供应商ID
     * @param ownId 所属店铺，默认-1
     * @param pageSize 每页尺寸 默认10
     * @param pageNo 当前页 默认0第一页
     * @param key 搜索关键字
     *            搜索范围：商品的标题，副标题，关键字，标签，供应商 可用空格分隔
     *            example：阿迪达斯T恤 equal to 阿迪达斯 T恤
     *            权重：按照 商品名称，关键字，品牌，分类名称，副标题，热点名称 顺序依次由高到低
     * @param  brands 品牌 （筛选项 品牌id |隔开）
     * @param category 分类 （筛选项 分类id |隔开）
     * @param typeIds 类目 筛选项 分类id |隔开）
     * @param  tags 商品标签（正品保证|海外直采|免税闪购|七天退换|海南直发）（筛选项 |隔开）
     * @param sorts 排序
     *              排序依据：新品，销量，价格 可组合排序，用|隔开
     *              example：
     *              按时间降序排序 = 1,
     *              按时间升序排序 = 2,
     *              按销售量降序排序 = 3,
     *              按销售量升序排序 = 4,
     *              按价格降序排序 = 5,
     *              按价格升序排序 = 6
     * @return 商品主键列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    ViewList search(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "supplierId",required = false) Long supplierId
            , @RequestParam(value = "ownId", defaultValue = "-1") Long ownId
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo
            , @RequestParam(value = "key", required = false) String key
            , @RequestParam(value = "brands", required = false) String brands
            , @RequestParam(value = "category", required = false) String category
            , @RequestParam(value = "typeIds", required = false) String typeIds
            , @RequestParam(value = "tags", required = false) String tags
            , @RequestParam(value = "sorts", required = false) String sorts);

    /***
     * 搜索建议
     * 在相关筛选条件下搜索关键字匹配热度最高的相关搜索词
     * @param customerId 商家Id
     * @param pageSize 返回的条数
     * @param key 关键字 (可以是拼音)
     * @return 搜索建议列表
     */
    @RequestMapping(value = "/suggest", method = RequestMethod.GET)
    @ResponseBody
    List<String> suggest(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "pageSize", required = false) Integer pageSize
            , @RequestParam(value = "key") String key
//            , @RequestParam(value = "brandId", required = false) Integer brandId
//            , @RequestParam(value = "categorieId", required = false) Integer categorieId
//            , @RequestParam(value = "hotspot", required = false) String hotspot

    );

    /**
     * 手动同步商品数据，如果 goodsId 为空则同步商户的所有数据；否则同步指定商品数据
     *
     * @param customerId 商户ID
     * @param goodsId    商品ID
     * @return
     */
    @RequestMapping(value = "/updateByMerchant", method = RequestMethod.POST)
    @ResponseBody
    String updateByMerchantIdAndGoodsId(@RequestParam(value = "customerId") Long customerId,
                                        @RequestParam(value = "goodsId", required = false) Long goodsId) throws IOException;
}
