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
public class ViewGoodsList {
     private  Paging paging;

    private List<ViewGoods> list;
}
