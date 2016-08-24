package com.huotu.huobanplus.search.model.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;

/**
 * 搜索热点
 * Created by Administrator on 2016/8/15.
 */
@Getter
@Setter
public class Hot {
    /***
     * 商家Id和搜索关键字主键
     *  customerId + '_' + key
     */
    @Field
    private String id;

    /***
     * 名称（用于匹配）
     *
     */
    @Field
    private String name;

    /**
     * 拼音
     */
    @Field
    private String pinyin;


    /***
     * 热度
     * 搜索一次算一个热度
     */
    @Field
    private Long hot;
}
