package com.huotu.huobanplus.search.entity;

import com.huotu.huobanplus.search.entity.pk.MallGoodsKeywordsPK;
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
@Table(name = "Mall_Goods_Keywords")
@IdClass(MallGoodsKeywordsPK.class)
public class MallGoodsKeywords {

    @Id
    @JoinColumn(name = "Goods_Id")
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private MallGoods mallGoods;


    @Id
    @Column(name = "Keyword")
    private String keyword;

    /**
     * 商家
     */
    @Column(name = "Customer_Id")
    private Long customerId;
}
