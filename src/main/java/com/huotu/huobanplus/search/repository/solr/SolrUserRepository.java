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

    public Long searchMaxId() {
        //因为主键只能保存为string类型，查询最大值不准确，所以用时间替代
        Pageable pageable = new SolrPageRequest(0, 1, new Sort(Sort.Direction.DESC, "regTime"));
        SimpleQuery query = new SimpleQuery("*:*", pageable);
        Page<User> maxUser = getSolrOperations().queryForPage(query, User.class);
        if (maxUser.getNumberOfElements() == 0) {
            return 0L;
        } else {
            return maxUser.getContent().get(0).getId();
        }
    }

    public Page<User> search(Long customerId, Integer pageSize, Integer pageNo,Integer exportSize
            , Integer levelId, Integer userType
            , String searchColumn, String fuzzySearchColumn, String searchValue
            , Integer minIntegral, Integer maxIntegral
            , Date searchBeginTime, Date searchEndTime
            , Boolean mobileBindRequired, String diyTags
            , String sortColumn, Sort.Direction sortDirect
    ) {
        Criteria criteria = new Criteria("customerId").is(customerId).and(new Criteria("deleted").is(false));
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
        if (minIntegral != null && minIntegral != -1) {
            criteria = criteria.and(new Criteria("userIntegral").greaterThanEqual(minIntegral));
        }
        if (maxIntegral != null && maxIntegral != -1) {
            criteria = criteria.and(new Criteria("userIntegral").lessThanEqual(maxIntegral));
        }
        if (searchBeginTime != null) {
            criteria = criteria.and(new Criteria("regTime").greaterThanEqual(searchBeginTime));
        }
        if (searchEndTime != null) {
            criteria = criteria.and(new Criteria("regTime").lessThan(searchEndTime));
        }
        if (StringUtils.isNotEmpty(searchColumn) && StringUtils.isNotEmpty(searchValue)) {
            criteria = criteria.and(new Criteria(searchColumn).is(searchValue));
        }
        if (StringUtils.isNotEmpty(fuzzySearchColumn) && StringUtils.isNotEmpty(searchValue)) {
            criteria = criteria.and(new Criteria(fuzzySearchColumn).contains(searchValue));
        }
//        Pageable pageable = new SolrPageRequest(pageNo, pageSize, new Sort(sortDirect, sortColumn));
        if(exportSize == null){
            exportSize = pageSize;
        }
        SimpleQuery query = new SimpleQuery(criteria)
                .setOffset(pageNo * pageSize)
                .setRows(exportSize)
                .addSort(new Sort(sortDirect,sortColumn));
        return getSolrOperations().queryForPage(query, User.class);
    }
}
