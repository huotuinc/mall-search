package com.huotu.huobanplus.search.model.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.rest.core.annotation.Description;

import java.util.Date;

/**
 * 用户表
 *
 * Created by helloztt on 2017-02-28.
 */
@Getter
@Setter
public class    User {

    @Field
    @Description("用户主键")
    private Long id;

    @Field
    @Description("商家Id")
    private Long customerId;

    @Field
    @Description("会员等级")
    private int levelId;

    @Field
    @Description("会员类型（0：普通会员，1：小伙伴）")
    private int userType;

    @Field
    @Description("登录名")
    private String loginName;

    @Field
    @Description("openId")
    private String openId;

    @Field
    @Description("从属小伙伴登录名")
    private String parentLoginName;

    @Field
    @Description("是否绑定手机")
    private boolean mobileBindRequired;

    @Field
    @Description("昵称,如果微信昵称为空则赋值为登录名")
    private String nickName;

    @Field
    @Description("姓名")
    private String realName;

    @Field
    @Description("余额（减去冻结）")
    private Double userBalance;

    @Field
    @Description("积分（减去冻结）")
    private Long userIntegral;

    @Field
    @Description("标签")
    private String diyTagIds;

    @Field
    @Description("注册时间")
    private Date regTime;




}
