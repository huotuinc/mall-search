package com.huotu.huobanplus.search.utils;

/**
 * 常量定义
 * Created by helloztt on 2017-03-02.
 */
public class Constant {
    /**
     * 如果设置太大可能会链接超时，所以把这个字段放在环境变量里面作为可配置项
     * 在{@link com.huotu.huobanplus.search.service.ScheduleService}里面初始化
     */
    public static int PAGE_SIZE = 100;
}
