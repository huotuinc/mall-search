package com.huotu.huobanplus.search.utils;

import com.huotu.huobanplus.common.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * 常用方法
 * Created by helloztt on 2017-03-18.
 */
public class ToolUtils {
    /**
     * 简单匹配
     * @param dateStr
     * @return
     */
    public static Date getDateFromString(String dateStr){
        Date date = null;
        if(StringUtils.isEmpty(dateStr)){
            return null;
        }
        if(dateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")){
            date = DateUtil.parse(dateStr,DateUtil.DATETIME_FORMAT);
        }else if(dateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")){
            date = DateUtil.parse(dateStr,"yyyy-MM-dd HH:mm");
        }else if(dateStr.matches("\\d{4}-\\d{2}-\\d{2}")){
            date = DateUtil.parse(dateStr,DateUtil.DATE_FORMAT);
        }
        return date;
    }

    public static Date getTomorrowDate(String dateStr){
        Date date = getDateFromString(dateStr);
        return getTomorrowDate(date);
    }

    public static  Date getTomorrowDate(Date today){
        if(today == null){
            return null;
        }
        return DateUtils.addDays(today,1);
    }
}
