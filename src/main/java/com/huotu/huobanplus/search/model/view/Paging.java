package com.huotu.huobanplus.search.model.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2016/8/15.
 */
@Setter
@Getter
@AllArgsConstructor
public class Paging {
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

}
