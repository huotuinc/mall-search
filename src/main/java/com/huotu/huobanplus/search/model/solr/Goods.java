package com.huotu.huobanplus.search.model.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.solr.core.mapping.Indexed;

import java.util.Date;

/**
 * 商品表
 * 模糊搜索字段权重：商品名称，关键字，品牌，分类名称，虚拟分类名称，副标题，热点名称。
 * 商品排序权重：销量，价格，上架时间。
 * Created by Administrator on 2016/8/15.
 */

@Getter
@Setter
public class Goods {

    @Field
    @Description("商品的Id")
    private Long id;

    @Field
    @Description("所属店铺")
    private Long ownerId;

    @Field
    @Description("商家Id")
    private Long customerId;

    @Field
    @Indexed(boost = 1.0f)
    @Description("标题，名称")
    private String title;

    @Field
    @Indexed(boost = 0.9f)
    @Description("关键字 用于商品搜索")
    private String keyword;

    @Field
    @Indexed(boost = 0.8f)
    @Description("品牌名称")
    private String brandName;

    @Field
    @Indexed(boost = 0.7f)
    @Description("分类名称")
    private String categoryName;

    // TODO: 2017-02-27  这个字段还没赋值，还不太了解虚拟分类，先放着
    @Field
    @Indexed(boost = 0.6f)
    @Description("虚拟分类")
    private String virtualCategory;

    @Field
    @Indexed(boost = 0.5f)
    @Description("标签")
    private String tags;

    @Field
    @Indexed(boost = 0.4f)
    @Description("商品描述(副标题)")
    private String description;

    @Field
    @Indexed(boost = 0.3f)
    @Description("热点(即销售标签)")
    private String hotspot;

    @Field
    @Description("品牌Id")
    private Long brandId;

    /**
     * 商品与分类是一对一关系，但是分类存在Path，这里保存分类ID的PATH，查询时按 "|" + categoryId + "|" 模糊查询
     */
    @Field
    @Description("分类Id")
    private String categoriesId;

    /**
     * 商品与标签是一对多关系，保存为字符串时前后以"|"连接；查询时按 "|" + tagId + "|" 模糊查询
     */
    @Field
    @Description("标签ID")
    private String tagIds;

    @Field
    @Description("新品(上架时间)")
    private Date updateTime;

    @Field
    @Description("销量")
    private Long sales;

    @Field
    @Description("原价")
    private Float originalPrice;

    @Field
    @Description("是否可用")
    private boolean disabled;
}
