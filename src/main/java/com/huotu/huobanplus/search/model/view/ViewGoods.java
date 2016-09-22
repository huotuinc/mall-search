package com.huotu.huobanplus.search.model.view;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/8/15.
 */

@Getter
@Setter
public class ViewGoods {

    /**
     * 商品的Id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 市场价
     */
    private Float price;

    /***
     * 原价
     */
    private Float originalPrice;

    /***
     * 图片地址
     */
    private String pictureUrl;

    /***
     * 概述
     */
    private String summary;

    /**
     * 返利价格信息
     */
    private String priceDesc;
}
