package com.huotu.huobanplus.search.model.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.rest.core.annotation.Description;

import java.util.Date;

/**
 * 订单表
 * Created by helloztt on 2017-03-16.
 */
@Getter
@Setter
public class Order {
    @Field
    @Description("订单号")
    private String id;

    @Field
    @Description("联合单号")
    private String unionOrderId;

    @Field
    @Description("商户ID")
    private Long customerId;

    @Field
    @Description("供应商ID")
    private Long supplierId;

    @Field
    @Description("用户名")
    private String userName;

    @Field
    @Description("收货人")
    private String receiver;

    @Field
    @Description("收货人电话")
    private String shipMobile;

    @Field
    @Description("下单时间")
    private Date createTime;

    @Field
    @Description("付款时间")
    private Date payTime;

    @Field
    @Description("订单状态")
    private Integer orderStatus;

    @Field
    @Description("支付状态")
    private Integer payStatus;

    @Field
    @Description("发货状态")
    private Integer shipStatus;

    @Field
    @Description("订单类型")
    private Integer sourceType;

    @Field
    @Description("商品名称")
    private String goodsName;

    @Field
    @Description("商品ID")
    private String goodsId;

    @Field
    @Description("支付类型")
    private Integer paymentType;

    @Field
    @Description("拼团是否成功，为1表示不成功")
    private Boolean shipDisabled;

    @Field
    @Description("订单金额")
    private Double finalAmount;
}
