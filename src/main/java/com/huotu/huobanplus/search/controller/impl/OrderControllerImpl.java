package com.huotu.huobanplus.search.controller.impl;

import com.huotu.huobanplus.search.controller.OrderController;
import com.huotu.huobanplus.search.model.view.ViewList;
import com.huotu.huobanplus.search.service.OrderService;
import com.huotu.huobanplus.search.utils.ToolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
public class OrderControllerImpl implements OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Override
    public ViewList search(
            @RequestParam(value = "customerId") Long customerId
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
            , @RequestParam(value = "sortDir", defaultValue = "1") Integer sortDir) {
        ViewList result = orderService.search(customerId,supplierId,pageSize,pageNo,
                orderId,unionOrderId,goodsName,userLoginName,shipName,shipMobile,
                ToolUtils.getDateFromString(createBeginTime),ToolUtils.getDateFromString(createEndTime),ToolUtils.getDateFromString(payBeginTime),ToolUtils.getDateFromString(payEndTime),
                payStatus,shipStatus,orderStatus,sourceType,payType,shipDisabled,getSortColumnFromSortType(sortType),getSortDirect(sortDir));
        return result;
    }

    @RequestMapping(value = "/updateByMerchant", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public String updateByMerchantIdAndOrderId(@RequestParam(value = "customerId") Long customerId, @RequestParam(value = "orderId", required = false) String orderId) throws IOException {
        if(StringUtils.isEmpty(orderId)){
            orderService.updateByCustomerId(customerId);
        }else{
            orderService.update(orderId);
        }
        return "success";
    }


    private String getSortColumnFromSortType(Integer sortType) {
        String sortColumn;
        switch (sortType) {
            case 0:
                sortColumn = "createTime";
                break;
            case 1:
                sortColumn = "payTime";
                break;
            case 2:
                sortColumn = "finalAmount";
                break;
            default:
                sortColumn = "createTime";
        }
        return sortColumn;
    }

    private Sort.Direction getSortDirect(Integer sortDir) {
        Sort.Direction sortDirect = Sort.Direction.DESC;
        if (sortDir == 0) {
            sortDirect = Sort.Direction.ASC;
        }
        return sortDirect;
    }
}
