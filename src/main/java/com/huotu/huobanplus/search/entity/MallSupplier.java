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
@Table(name = "Mall_Supplier")
public class MallSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Supplier_Id")
    private Long id;

    @Column(name = "Name")
    private String name;

    /**
     * 商家
     */
    @Column(name = "Customer_Id")
    private Long customerId;
}
