package com.huotu.huobanplus.search.repository.solr;

import com.huotu.huobanplus.search.model.solr.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.util.StringUtils;

/**
 * Created by Administrator on 2016/8/17.
 */
@NoRepositoryBean
public class SolrGoodsRepository extends SimpleSolrRepository<Goods, Long> {

    public Page<Goods> search(Long customerId, Integer pageSize, Integer page, Integer levelId
            , String key, String brands, String category, String hotspot, Integer sorts) {


        Criteria criteria = new Criteria("customerId").is(customerId);
        if (!StringUtils.isEmpty(brands)) {
            criteria = criteria.and(new Criteria("brands").is(brands));
        }
        if (!StringUtils.isEmpty(category)) {
            criteria = criteria.and(new Criteria("category").is(category));
        }
        if (!StringUtils.isEmpty(hotspot)) {
            criteria = criteria.and(new Criteria("hotspot").is(hotspot));
        }

        //权重：按照 标题，副标题，关键字，标签，虚拟分类，供应商 顺序依次由高到低
        if (!StringUtils.isEmpty(key)) {
//            Criteria criteria1 = new Criteria("title").expression(Criteria.WILDCARD).contains(key);
            Criteria criteria1 = new Criteria("title").is(key);
            criteria1 = criteria1.or(new Criteria("description").is(key));
            criteria1 = criteria1.or(new Criteria("keyword").is(key));
            criteria1 = criteria1.or(new Criteria("tags").is(key));
//            criteria1 = criteria1.or(new Criteria("virturalCatetory").is(key));
            criteria1 = criteria1.or(new Criteria("supplier")).is(key);
            criteria = criteria.and(criteria1);
        }
        //todo levelid query


        //defalut sort updateTime;
        //1代表 上架时间降序 2销量降序 3价格升序 4价格降序
        Sort sort = null;
        if (sorts == 1) sort = new Sort(Sort.Direction.DESC, "updateTime");
        if (sorts == 2) sort = new Sort(Sort.Direction.DESC, "sales");
        if (sorts == 3) sort = new Sort(Sort.Direction.ASC, "memberPrice");
        if (sorts == 4) sort = new Sort(Sort.Direction.DESC, "memberPrice");

        Pageable pageable = new SolrPageRequest(page, pageSize, sort);
        SimpleQuery query = new SimpleQuery(criteria, pageable);
//        FacetQuery simpleFacetQuery = new SimpleFacetQuery();
//        query.setFacetOptions();
//        query.addFilterQuery();
        return getSolrOperations().queryForPage(query, Goods.class);
    }

}
