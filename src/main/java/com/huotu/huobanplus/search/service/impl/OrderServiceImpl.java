package com.huotu.huobanplus.search.service.impl;

import com.huotu.huobanplus.sdk.common.repository.OrdersRestRepository;
import com.huotu.huobanplus.search.model.solr.Order;
import com.huotu.huobanplus.search.model.view.ViewList;
import com.huotu.huobanplus.search.repository.solr.SolrOrderRepository;
import com.huotu.huobanplus.search.service.OrderService;
import com.huotu.huobanplus.search.utils.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2017-03-16.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static final Log log = LogFactory.getLog(OrderServiceImpl.class);
    @Autowired
    private SolrOrderRepository solrOrderRepository;
    @Autowired
    private OrdersRestRepository ordersRestRepository;


    @Override
    public ViewList search(Long customerId, Long supplierId, Integer pageSize, Integer pageNo,
                           String orderId, String unionOrderId, String goodsName, String userLoginName, String shipName, String shipMobile,
                           Date createBeginTime, Date createEndTime, Date payBeginTime, Date payEndTime,
                           Integer payStatus, Integer shipStatus, Integer orderStatus, Integer sourceType, Integer payType,
                           Boolean shipDisabled, String sortColumn, Sort.Direction sortDirect) {
        Page<Order> orderPage = solrOrderRepository.search(customerId, supplierId, pageSize, pageNo,
                orderId, unionOrderId, userLoginName, shipName, shipMobile,
                payStatus, shipStatus, orderStatus, sourceType, shipDisabled,
                createBeginTime, createEndTime, payBeginTime, payEndTime, sortColumn, sortDirect);
        ViewList viewOrderList = new ViewList();
        viewOrderList.setPageSize(pageSize);
        viewOrderList.setPage(pageNo);
        viewOrderList.setRecordCount(orderPage.getTotalElements());
        if(orderPage.getNumberOfElements() != 0){
            String[] ids = new String[orderPage.getNumberOfElements()];
            for(int i=0;i< orderPage.getNumberOfElements();i++){
                ids[i] = orderPage.getContent().get(i).getId();
            }
            viewOrderList.setIds(ids);
        }
        return viewOrderList;
    }

    @Override
    public String maxId() {
        String maxId = solrOrderRepository.searchMaxId();
        if(maxId == null){
            maxId = "";
        }
        return maxId;
    }

    @Override
    public void update(String orderId) throws IOException {
        com.huotu.huobanplus.common.entity.Order mallOrder = ordersRestRepository.getOneByPK(orderId);
        if (mallOrder != null) {
            update(mallOrder);
        }
    }

    @Override
    public void updateByCustomerId(Long customerId) throws IOException {
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            Page<com.huotu.huobanplus.common.entity.Order> mallOrderList = ordersRestRepository.search(customerId, new PageRequest(pageNo, pageSize));
            if (mallOrderList.getNumberOfElements() == 0) {
                break;
            }
            update(mallOrderList.getContent());
            pageNo++;
        }
    }

    @Override
    public void update(com.huotu.huobanplus.common.entity.Order mallOrder) throws IOException {
        Order solrOrder = solrOrderRepository.findOne(mallOrder.getId());
        solrOrder = mallOrderToSolrOrder(mallOrder, solrOrder);
        if (solrOrder != null) {
            solrOrderRepository.save(solrOrder);
        }
    }

    @Override
    public void update(List<com.huotu.huobanplus.common.entity.Order> orderList) throws IOException {
        if (orderList == null || orderList.size() == 0) {
            return;
        }
        List<Order> solrOrderList = new ArrayList<>();
        orderList.forEach(mallOrder -> {
            Order solrOrder = solrOrderRepository.findOne(mallOrder.getId());
            try {
                solrOrder = mallOrderToSolrOrder(mallOrder, solrOrder);
            } catch (IOException e) {
                log.error("orderId:" + mallOrder.getId(), e);
            }
            if (solrOrder != null) {
                solrOrderList.add(solrOrder);
            }
        });
        solrOrderRepository.save(solrOrderList);
    }

    private Order mallOrderToSolrOrder(com.huotu.huobanplus.common.entity.Order mallOrder, Order solrOrder) throws IOException {
        if (mallOrder == null) {
            return null;
        }
        if (solrOrder == null) {
            solrOrder = new Order();
            solrOrder.setId(mallOrder.getId());
        }
        solrOrder.setUnionOrderId(mallOrder.getMainOrderNo());
        if (mallOrder.getMerchant() != null) {
            solrOrder.setCustomerId(mallOrder.getMerchant().getId());
        }
        solrOrder.setSupplierId(mallOrder.getSupplierId());
        solrOrder.setUserName(mallOrder.getUserName());
        solrOrder.setReceiver(mallOrder.getReceiver());
        solrOrder.setShipMobile(mallOrder.getShipMobile());
        solrOrder.setCreateTime(mallOrder.getTime());
        solrOrder.setPayTime(mallOrder.getPayTime());
        solrOrder.setOrderStatus(mallOrder.getStatus());
        solrOrder.setPayStatus(mallOrder.getPayStatus());
        solrOrder.setShipStatus(mallOrder.getDeliverStatus());
        solrOrder.setSourceType(mallOrder.getSourceType());
        if (mallOrder.getGoodsNameList() != null) {
            StringBuilder sb = new StringBuilder("|");
            mallOrder.getGoodsNameList().forEach(name -> {
                sb.append(name).append("|");
            });
            solrOrder.setGoodsName(sb.toString());
        }
        solrOrder.setPaymentType(mallOrder.getPayType());
        solrOrder.setShipDisabled(mallOrder.getShipDisabled());
        solrOrder.setFinalAmount(mallOrder.getPrice());
        return solrOrder;
    }
}
