package com.huotu.huobanplus.search.controller.impl;

import com.huotu.huobanplus.common.utils.DateUtil;
import com.huotu.huobanplus.search.controller.UserController;
import com.huotu.huobanplus.search.model.view.ViewList;
import com.huotu.huobanplus.search.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Date;

/**
 * Created by helloztt on 2017-02-28.
 */
@Controller
public class UserControllerImpl implements UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @Override
    public ViewList search(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo
            , @RequestParam(value = "levelId", required = false) Integer levelId
            , @RequestParam(value = "userType", required = false) Integer userType
            , @RequestParam(value = "searchType", required = false) Integer searchType
            , @RequestParam(value = "searchValue", required = false) String searchValue
            , @RequestParam(value = "minIntegral", required = false) Integer minIntegral
            , @RequestParam(value = "maxIntegral", required = false) Integer maxIntegral
            , @RequestParam(value = "txtBeginTime", required = false) String txtBeginTime
            , @RequestParam(value = "txtEndTime", required = false) String txtEndTime
            , @RequestParam(value = "mobileBindRequired", required = false) Boolean mobileBindRequired
            , @RequestParam(value = "diyTags", required = false) String diyTags
            , @RequestParam(value = "sortType", defaultValue = "0") Integer sortType
            , @RequestParam(value = "sortDir", defaultValue = "1") Integer sortDir) {

        ViewList result = userService.search(customerId, pageSize, pageNo
                , levelId, userType
                , getSearchColumnFromSearchType(searchType), getSearchColumnFromFuzzySearchType(searchType), searchValue
                , minIntegral, maxIntegral, getBeginDateFromString(txtBeginTime), getEndDateFromString(txtEndTime)
                , mobileBindRequired, diyTags,
                getSortColumnFromSortType(sortType), getSortDirect(sortDir));
        return result;
    }

    @Override
    public String updateByMerchantIdAndGoodsId(@RequestParam(value = "customerId") Long customerId, @RequestParam(value = "userId", required = false) Long userId) throws IOException {
        if(userId == null){
            userService.updateByCustomerId(customerId);
        }else{
            userService.update(userId);
        }
        return "success";
    }

    private String getSearchColumnFromSearchType(Integer searchType) {
        if (searchType == null) {
            return null;
        }
        String searchColumn = null;
        switch (searchType) {
            case 1:
                searchColumn = "loginName";
                break;
            case 3:
                searchColumn = "parentLoginName";
                break;
            case 5:
                searchColumn = "openId";
                break;
        }
        return searchColumn;
    }

    private String getSearchColumnFromFuzzySearchType(Integer fuzzySearchType){
        if (fuzzySearchType == null) {
            return null;
        }
        String searchColumn = null;
        switch (fuzzySearchType) {
            case 2:
                searchColumn = "realName";
                break;
            case 4:
                searchColumn = "nickName";
                break;
        }
        return searchColumn;
    }

    private String getSortColumnFromSortType(Integer sortType) {
        String sortColumn;
        switch (sortType) {
            case 0:
                sortColumn = "regTime";
                break;
            case 1:
                sortColumn = "userIntegral";
                break;
            case 2:
                sortColumn = "userBalance";
                break;
            default:
                sortColumn = "regTime";
        }
        return sortColumn;
    }

    private Date getBeginDateFromString(String dateStr) {
        Date date = null;
        if (StringUtils.isNotEmpty(dateStr)) {
            date = DateUtil.parse(dateStr, DateUtil.DATETIME_FORMAT);
        }
        return date;
    }

    private Date getEndDateFromString(String dateStr){
        Date date = null;
        if (StringUtils.isNotEmpty(dateStr)) {
            dateStr += ".999";
            date = DateUtil.parse(dateStr, DateUtil.DATETIME_FORMAT + ".SSS");
        }
        return date;
    }

    private Sort.Direction getSortDirect(Integer sortDir) {
        Sort.Direction sortDirect = Sort.Direction.DESC;
        if (sortDir == 0) {
            sortDirect = Sort.Direction.ASC;
        }
        return sortDirect;
    }
}
