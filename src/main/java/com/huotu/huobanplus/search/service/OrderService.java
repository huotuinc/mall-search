package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.common.entity.Order;
import com.huotu.huobanplus.search.model.view.ViewList;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2017-03-16.
 */
public interface OrderService {
    ViewList search(Long customerId, Long supplierId, Integer pageSize, Integer pageNo, Integer exportSize
            , String orderId, String unionOrderId, String goodsName, Integer goodsId, String userLoginName, String shipName, String shipMobile
            , Date createBeginTime, Date createEndTime, Date payBeginTime, Date payEndTime
            , Integer payStatus, Integer shipStatus, Integer orderStatus
            , Integer sourceType, Integer payType, Boolean shipDisabled
            , String sortColumn, Sort.Direction sortDirect);

    String maxId();

    void update(String orderId) throws IOException;

    void updateByCustomerId(Long customerId) throws IOException;

//    void update(Order order) throws IOException;

    void update(List<Order> orderList) throws IOException;
}
