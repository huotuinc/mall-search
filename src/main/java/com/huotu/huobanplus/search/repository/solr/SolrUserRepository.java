package com.huotu.huobanplus.search.repository.solr;

import com.huotu.huobanplus.search.model.solr.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;

import java.util.Date;

/**
 * Created by helloztt on 2017-02-28.
 */
public class SolrUserRepository extends SimpleSolrRepository<User, Long> {

    public Page<User> search(Long customerId, Integer pageSize, Integer pageNo
            , Integer levelId, Integer userType
            , String searchColumn, String fuzzySearchColumn, String searchValue
            , Integer minIntegral, Integer maxIntegral
            , Date searchBeginTime, Date searchEndTime
            , Boolean mobileBindRequired, String diyTags
            , String sortColumn, Sort.Direction sortDirect
    ) {
        Criteria criteria = new Criteria("customerId").is(customerId);
        if (levelId != null) {
            criteria = criteria.and(new Criteria("levelId").is(levelId));
        }
        if (userType != null) {
            criteria = criteria.and(new Criteria("userType").is(userType));
        }
        if (mobileBindRequired != null) {
            criteria = criteria.and(new Criteria("mobileBindRequired").is(mobileBindRequired));
        }
        if (StringUtils.isNotEmpty(diyTags)) {
            criteria = criteria.and(new Criteria("diyTagIds").is(diyTags));
        }
        if (minIntegral != null) {
            criteria = criteria.and(new Criteria("userIntegral").greaterThanEqual(minIntegral));
        }
        if (maxIntegral != null) {
            criteria = criteria.and(new Criteria("userIntegral").lessThanEqual(maxIntegral));
        }
        if (searchBeginTime != null) {
            criteria = criteria.and(new Criteria("regTime").greaterThanEqual(searchBeginTime));
        }
        if (searchEndTime != null) {
            criteria = criteria.and(new Criteria("regTime").lessThanEqual(searchEndTime));
        }
        if (StringUtils.isNotEmpty(searchColumn) && StringUtils.isNotEmpty(searchValue)) {
            criteria = criteria.and(new Criteria(searchColumn).is(searchValue));
        }
        if (StringUtils.isNotEmpty(fuzzySearchColumn) && StringUtils.isNotEmpty(searchValue)) {
            criteria = criteria.and(new Criteria(fuzzySearchColumn).contains(searchValue));
        }
        Pageable pageable = new SolrPageRequest(pageNo, pageSize, new Sort(sortDirect, sortColumn));
        SimpleQuery query = new SimpleQuery(criteria, pageable);
        return getSolrOperations().queryForPage(query, User.class);
    }
}
