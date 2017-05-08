package com.huotu.huobanplus.search.repository.solr;

import com.huotu.huobanplus.search.model.solr.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Created by helloztt on 2017-03-16.
 */
public class SolrOrderRepository extends SimpleSolrRepository<Order, String> {

    public String searchMaxId() {
        Pageable pageable = new SolrPageRequest(0, 1, new Sort(Sort.Direction.DESC, "id"));
        SimpleQuery query = new SimpleQuery("*:*", pageable);
        Page<Order> maxUser = getSolrOperations().queryForPage(query, Order.class);
        if (maxUser.getNumberOfElements() == 0) {
            return null;
        } else {
            return maxUser.getContent().get(0).getId();
        }
    }

    public Page<Order> search(Long customerId, Long supplierId, Integer pageSize, Integer pageNo, Integer exportSize
            , String orderId, String unionOrderId
            , String goodsName, Integer goodsId
            , String userName, String shipName, String shipMobile
            , Integer payStatus, Integer shipStatus, Integer orderStatus
            , Integer sourceType, Boolean shipDisabled
            , Date createBeginTime, Date createEndTime, Date payBeginTime, Date payEndTime
            , String sortColumn, Sort.Direction sortDirect
    ) {
        Criteria criteria = new Criteria("customerId").is(customerId);
        if (supplierId != null && supplierId > 0) {
            criteria = criteria.and(new Criteria("supplierId").is(supplierId));
        }
        if (!StringUtils.isEmpty(orderId)) {
            criteria = criteria.and(new Criteria("id").is(orderId));
        }
        if (!StringUtils.isEmpty(unionOrderId)) {
            criteria = criteria.and(new Criteria("unionOrderId").is(unionOrderId));
        }
        if(!StringUtils.isEmpty(goodsName)){
            criteria = criteria.and(new Criteria("goodsName").is(goodsName));
        }
        if(goodsId != null && goodsId > 0){
            criteria = criteria.and(new Criteria("goodsId").is(goodsId));
        }
        if (!StringUtils.isEmpty(userName)) {
            criteria = criteria.and(new Criteria("userName").is(userName));
        }
        if (!StringUtils.isEmpty(shipName)) {
            criteria = criteria.and(new Criteria("receiver").contains(shipName));
        }
        if (!StringUtils.isEmpty(shipMobile)) {
            criteria = criteria.and(new Criteria("shipMobile").is(shipMobile));
        }
        if (payStatus != null) {
            criteria = criteria.and(new Criteria("payStatus").is(payStatus));
        }
        if (shipStatus != null) {
            criteria = criteria.and(new Criteria("shipStatus").is(shipStatus));
        }
        if (orderStatus != null) {
            criteria = criteria.and(new Criteria("orderStatus").is(orderStatus));
        }
        if (sourceType != null) {
            criteria = criteria.and(new Criteria("sourceType").is(sourceType));
        }
        if (shipDisabled != null) {
            criteria = criteria.and(new Criteria("shipDisabled").is(shipDisabled));
        }
        if (createBeginTime != null) {
            criteria = criteria.and(new Criteria("createTime").greaterThanEqual(createBeginTime));
        }
        if (createEndTime != null) {
            criteria = criteria.and(new Criteria("createTime").lessThan(createEndTime));
        }
        if (payBeginTime != null) {
            criteria = criteria.and(new Criteria("payTime").greaterThanEqual(payBeginTime));
        }
        if (payEndTime != null) {
            criteria = criteria.and(new Criteria("payTime").lessThan(payEndTime));
        }
        if (exportSize == null) {
            exportSize = pageSize;
        }
        SimpleQuery query = new SimpleQuery(criteria)
                .setOffset(pageNo * pageSize)
                .setRows(exportSize)
                .addSort(new Sort(sortDirect, sortColumn));
//        Pageable pageable = new SolrPageRequest(pageNo, pageSize, new Sort(sortDirect, sortColumn));
//        SimpleQuery query = new SimpleQuery(criteria, pageable);
        return getSolrOperations().queryForPage(query, Order.class);
    }
}
