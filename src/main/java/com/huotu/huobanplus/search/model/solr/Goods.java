package com.huotu.huobanplus.search.model.solr;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long ownerId = 0L;

    @Field
    @Description("商家Id")
    private Long customerId = 0L;

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
    private Long brandId = 0L;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Field
    @Description("销量")
    private Long sales = 0L;

    @Field
    @Description("原价")
    private Float originalPrice = 0F;

    @Field
    @Description("是否可用")
    private boolean disabled;

    /**
     * @since 1.1
     */
    @Field
    @Description("类型ID")
    private Long typeId = 0L;

    @Field
    @Description("分类ID")
    private Long catId = 0L;

    @Field
    @Description("供应商ID")
    private Long supplierId = 0L;

    @Field
    @Description("商品主图，小图")
    private String smallPic;

    @Field
    @Description("简介")
    private String brief;

    @Field
    @Description("市场价")
    private double marketPrice;

    @Field
    @Description("商品价格")
    private double price;

    @Field
    @Description("商品规格冗余")
    private String spec;

    @Field
    @Description("货品规格冗余")
    private String pdtDesc;

    @Field
    @Description("商品规格")
    private String specDesc;

    @Field
    @Description("专享价类型")
    private Integer vipRebateType = 0;

    @Field
    @Description("专享价")
    private String vipRebateItems;

    @Field
    @Description("各个会员级别的价格冗余")
    private String priceLevelDesc;

    @Field
    @Description("八级返利的冗余字段")
    private String disRebateDesc;

    @Field
    @Description("个性化经营者返利配置信息")
    private String customTmrRebateSetting;

    @Field
    @Description("商品返利是否个性化")
    private Integer individuation;

    @Field
    @Description("返利计算模式")
    @JsonProperty(defaultValue = "0")
    private Integer rebateMode = 0;

    @Field
    @Description("返利设置(按配额)")
    @JsonProperty("rebateQuatoSetting")
    private String rebateQuotaSetting;

    @Field
    @Description("返利参数，按配额返利时的参数")
    @JsonProperty(value = "rebateQuatoRatio")
    private Double rebateQuotaRatio = 0D;

    @Field
    @Description("返利设置(按销售额)")
    private String rebateSaleSetting;

    @Field
    @Description("返利参数，按销售额返利时的返利系数")
    private Double rebateSaleRatio = 0D;


    @Field
    @Description("是否个性化经营者返利")
    @JsonProperty(value = "customTmrRebateFlag")
    private Integer customManagerRebateFlag = 0;



}
