package com.huotu.huobanplus.search.controller;

import com.huotu.huobanplus.search.model.view.ViewList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by helloztt on 2017-03-16.
 */
@Controller
@RequestMapping("/order")
public interface OrderController {

    /**
     * 订单搜索，用于商城后台搜索
     *
     * @param customerId      商户ID，必填
     * @param pageSize        每页尺寸 默认10
     * @param pageNo          当前页 默认0第一页
     * @param orderId         订单号，精准匹配
     * @param unionOrderId    联合订单号，精准匹配
     * @param userLoginName   用户名，精准匹配
     * @param shipName        收货人姓名,模糊匹配
     * @param shipMobile      收货人电话，精准匹配
     * @param createBeginTime 下单起始时间，支持yyyy-MM-dd，yyyy-MM-dd HH:mm , yyyy-MM-dd HH:mm:ss格式
     * @param createEndTime   下单结束时间，支持yyyy-MM-dd，yyyy-MM-dd HH:mm , yyyy-MM-dd HH:mm:ss格式
     * @param payBeginTime    支付起始时间，支持yyyy-MM-dd，yyyy-MM-dd HH:mm , yyyy-MM-dd HH:mm:ss格式
     * @param payEndTime      支持结束时间，支持yyyy-MM-dd，yyyy-MM-dd HH:mm , yyyy-MM-dd HH:mm:ss格式
     * @param payStatus       支付状态
     * @param shipStatus      发货状态
     * @param supplierId      供应商主键
     * @param sourceType      订单类型
     * @param goodsName       商品名称。分词搜索
     * @param payType         支付类型
     * @param shipDisabled    是否成团，1表示否；0表示成团
     * @param orderStatus     订单状态
     * @param sortType        排序字段：0：按下单时间；1：按支付时间；2：按订单金额
     * @param sortDir         排序类型 0：升序 1：降序（默认）
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ViewList search(@RequestParam(value = "customerId") Long customerId
            , @RequestParam(value = "supplierId", required = false) Long supplierId
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo
            , @RequestParam(value = "orderId", required = false) String orderId
            , @RequestParam(value = "unionOrderId", required = false) String unionOrderId
            , @RequestParam(value = "goodsName", required = false) String goodsName
            , @RequestParam(value = "userLoginName", required = false) String userLoginName
            , @RequestParam(value = "shipName", required = false) String shipName
            , @RequestParam(value = "shipMobile", required = false) String shipMobile
            , @RequestParam(value = "beginTime", required = false) String createBeginTime
            , @RequestParam(value = "endTime", required = false) String createEndTime
            , @RequestParam(value = "payBeginTime", required = false) String payBeginTime
            , @RequestParam(value = "payEndTime", required = false) String payEndTime
            , @RequestParam(value = "payStatus", required = false) Integer payStatus
            , @RequestParam(value = "shipStatus", required = false) Integer shipStatus
            , @RequestParam(value = "orderStatus", required = false) Integer orderStatus
            , @RequestParam(value = "sourceType", required = false) Integer sourceType
            , @RequestParam(value = "payType", required = false) Integer payType
            , @RequestParam(value = "shipDisabled", required = false) Boolean shipDisabled
            , @RequestParam(value = "sortType", defaultValue = "0") Integer sortType
            , @RequestParam(value = "sortDir", defaultValue = "1") Integer sortDir
    );

    /**
     * 手动同步用户数据，如果 userId 为空则同步商户的所有数据；否则同步指定用户数据
     *
     * @param customerId 商户ID
     * @param orderId    商品ID
     * @return
     */
    @RequestMapping(value = "/updateByMerchant", method = RequestMethod.POST)
    @ResponseBody
    String updateByMerchantIdAndOrderId(@RequestParam(value = "customerId") Long customerId,
                                        @RequestParam(value = "orderId", required = false) String orderId) throws IOException;
}
