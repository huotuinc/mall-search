package com.huotu.huobanplus.search.model.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Date;

/**
 * 商品表
 * Created by Administrator on 2016/8/15.
 */

@Getter
@Setter
public class Goods {

    /**
     * 商品的Id
     */
    @Field
    private Long id;

    /**
     * 商家Id
     */
    @Field
    private Long customerId;


    /**
     * 标题
     */
    @Field
    @Indexed(boost = 1.0f)
    private String title;
    /**
     * 市场价
     */
    @Field
    private Float price;
    /***
     * 原价
     */
    @Field
    private Float originalPrice;
    /***
     * 会员价
     */
    @Field
    private Float memberPrice;

    /***
     * 图片地址
     */
    @Field
    private String pictureUrl;


    /**
     * 商品描述(副标题)
     */
    @Field
    @Indexed(boost = 0.9f)
    private String description;
    /**
     * 关键字 用于商品搜索
     */
    @Field
    @Indexed(boost = 0.8f)
    private String keyword;
    /***
     * 供应商
     */
    @Field
    @Indexed(boost = 0.5f)
    private String supplier;
    /***
     * 标签
     */
    @Field
    @Indexed(boost = 0.7f)
    private String tags;
//    /***
//     * 虚拟分类
//     */
//    @Field
//    @Indexed(boost = 0.6f)
//    private String virturalCatetory;


    /***
     * 品牌Id
     */
    @Field
    private Long brandsId;
    /***
     * 分类Id
     */
    @Field
    private Long categoryId;
    /***
     * 热点(即销售标签)
     */
    @Field
    private String hotspot;
//    /***
//     * 正品保障
//     */
//    private Boolean security;
//    /***
//     * 海外直购
//     */
//    private Boolean overseas;
//    /***
//     * 免税
//     */
//    private Boolean freeTax;
//    /***
//     * 七天退换
//     */
//    private Boolean sevenDayReturn;
//    /***
//     * 海南直发
//     */
//    private Boolean hainanStraight;


    /***
     * 新品(上架时间)
     */
    @Field
    private Date updateTime;

    /***
     * 销量
     */
    @Field
    private Long sales;


    /***
     * 价格信息
     */
    @Field
    private String priceDesc;

    /**
     * 返利信息
     */
    @Field
    private String rebateDesc;
}
