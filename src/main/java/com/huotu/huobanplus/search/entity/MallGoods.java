package com.huotu.huobanplus.search.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Administrator on 2016/8/24.
 */
@Entity
@Getter
@Setter
@Cacheable(value = false)
@Table(name = "Mall_Goods")
public class MallGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Goods_Id")
    private Long id;

    /**
     * 商家
     */
    @Column(name = "Customer_Id")
    private Long customerId;

    /**
     * 商品标题
     */
    @Column(name = "Name")
    private String title;

    /**
     * 副标题
     */
    @Column(name = "Subtitle")
    private String description;

    /**
     * 商品价格
     */
    @Column(name = "Price")
    private float price;

    /**
     * 市场价
     */
    @Column(name = "Mktprice")
    private float marketPrice;

    /**
     * 小图
     */
    @Column(name = "Small_Pic")
    private String smallPic;

    /***
     * 品牌Id
     */
    @Column(name = "Brand_Id")
    private Long brandId;
    /**
     * 供应商
     */
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "Supplier_Id")
    private MallSupplier supplier;

    /***
     * 分类Id
     */
    @Column(name = "Cat_Id")
    private Long categoryId;

    /**
     * 1自定义 0 全局
     */
    @Column(name = "SellTags_Custom")
    private Integer sellTagsCustom;
    /**
     * 销售标签
     */
    @Column(name = "SellTags")
    private String sellTags;

}
