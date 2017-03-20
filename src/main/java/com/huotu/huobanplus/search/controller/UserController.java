package com.huotu.huobanplus.search.controller;

import com.huotu.huobanplus.search.model.view.ViewList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by helloztt on 2017-02-28.
 */
@RequestMapping("/user")
public interface UserController {

    /**
     * 用户搜索，用于商城后台搜索
     *
     * @param customerId         商户ID
     * @param pageSize           每页尺寸 默认10
     * @param pageNo             当前页 默认0第一页
     * @param levelId            用户级别，是否绑定手机号
     *                           -102：已绑定手机；-103：未绑定手机
     * @param userType           用户类型
     * @param searchType         搜索列类型
     *                           精准搜索：1：登录名（UB_UserLoginName）3：从属小伙伴登录名 5：openId
     *                           模糊搜索：2：姓名（UB_UserRealName）4:昵称（UB_WxNickName）
     * @param searchValue        搜索列值
     * @param minIntegral        积分搜索区间，默认为-1表示不搜索
     * @param maxIntegral        积分搜索区间，默认为-1表示不搜索
     * @param txtBeginTime       注册时间搜索区间
     * @param txtEndTime         注册时间搜索区间
     * @param diyTags            会员标签（筛选项 标签id |隔开）
     * @param sortType           排序字段
     *                           0：注册时间（默认） 1：积分 2：余额
     * @param sortDir            排序类型 0：升序 1：降序（默认）
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ViewList search(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo
            , @RequestParam(value = "levelId", required = false) Integer levelId
            , @RequestParam(value = "userType", required = false) Integer userType
            , @RequestParam(value = "searchType", required = false) Integer searchType
            , @RequestParam(value = "searchValue", required = false) String searchValue
            , @RequestParam(value = "minIntegral", defaultValue = "-1") Integer minIntegral
            , @RequestParam(value = "maxIntegral", defaultValue = "-1") Integer maxIntegral
            , @RequestParam(value = "txtBeginTime", required = false) String txtBeginTime
            , @RequestParam(value = "txtEndTime", required = false) String txtEndTime
            , @RequestParam(value = "diyTags", required = false) String diyTags
            , @RequestParam(value = "sortType", defaultValue = "0") Integer sortType
            , @RequestParam(value = "sortDir", defaultValue = "1") Integer sortDir
    );

    /**
     * 手动同步用户数据，如果 userId 为空则同步商户的所有数据；否则同步指定用户数据
     *
     * @param customerId 商户ID
     * @param userId    商品ID
     * @return
     */
    @RequestMapping(value = "/updateByMerchant", method = RequestMethod.POST)
    @ResponseBody
    String updateByMerchantIdAndUserId(@RequestParam(value = "customerId") Long customerId,
                                       @RequestParam(value = "userId", required = false) Long userId) throws IOException;
}
