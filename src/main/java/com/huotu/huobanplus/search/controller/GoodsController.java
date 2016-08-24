package com.huotu.huobanplus.search.controller;

import com.huotu.huobanplus.search.model.enums.UpdateFrequency;
import com.huotu.huobanplus.search.model.view.ViewGoodsList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */

@RequestMapping("/goods")
public interface GoodsController {


    /***
     * 商品搜索
     * 同时搜索词进入搜索热度
     * @param customerId 商家Id
     * @param pageSize 每页尺寸 默认10
     * @param page 当前页 默认0第一页
     *             @param levelId 会员等级
     * @param key 搜索关键字
     *            搜索范围：商品的标题，副标题，关键字，标签，虚拟分类，供应商 可用空格分隔 (虚拟分类暂不考虑)
     *            example：阿迪达斯T恤 equal to 阿迪达斯 T恤
     *            权重：按照 标题，副标题，关键字，标签，虚拟分类，供应商 顺序依次由高到低
     * @param  brandsId 品牌Id （筛选项）
     * @param categoryId 分类Id （筛选项）
     * @param  hotspot 热点（正品保证、海外直采，免税闪购，七天退换，海南直发）（筛选项）
     * @param sorts 排序
     *              排序依据：新品，销量，价格
     *              example： 3（1代表 上架时间降序 2销量降序 3价格升序 4价格降序）（默认无）
     * @return 商品列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    ViewGoodsList search(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "pageSize", required = false) Integer pageSize
            , @RequestParam(value = "page", required = false) Integer page
            , @RequestParam(value = "levelId", required = false) Integer levelId
            , @RequestParam(value = "key", required = false) String key
            , @RequestParam(value = "brandsId", required = false) Integer brandsId
            , @RequestParam(value = "categoryId", required = false) Integer categoryId
            , @RequestParam(value = "hotspot", required = false) String hotspot
            , @RequestParam(value = "sorts", required = false) Integer sorts

    );

    /***
     * 搜索建议
     * 在相关筛选条件下搜索关键字匹配热度最高的相关搜索词
     * @param customerId 商家Id
     *                   @param pageSize 返回的条数
     * @param key 关键字 (可以是拼音)
     * @return 搜索建议列表
     */
    @RequestMapping(value = "/suggest", method = RequestMethod.GET)
    List<String> suggest(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "pageSize", required = false) Integer pageSize
            , @RequestParam(value = "key") String key
//            , @RequestParam(value = "brandsId", required = false) Integer brandsId
//            , @RequestParam(value = "categoryId", required = false) Integer categoryId
//            , @RequestParam(value = "hotspot", required = false) String hotspot

    );


    /***
     *
     * @param id 商品Id
     * @param updateFrequency 更新频率 默认
     */
    @RequestMapping(value = "/udpate", method = RequestMethod.POST)
    void update(@RequestParam(value = "id") Long id
            , @RequestParam(value = "updateFrequency", required = false) UpdateFrequency updateFrequency);

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    String test();
}
