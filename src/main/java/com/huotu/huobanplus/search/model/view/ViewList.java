package com.huotu.huobanplus.search.model.view;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */
@Getter
@Setter
public class ViewList {
     /***
      * 每页尺寸
      */
     private Integer pageSize;
     /***
      * 当前页（默认0）
      */
     private Integer page;
     /***
      * 总记录数
      */
     private Long recordCount;
     /**
      * 总页面数
      */
     private int pageCount;
//     private  Paging paging;
     private Object[] ids;

    private List list;
}
