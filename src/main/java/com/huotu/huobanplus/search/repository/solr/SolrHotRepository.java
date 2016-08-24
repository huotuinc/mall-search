package com.huotu.huobanplus.search.repository.solr;

import com.huotu.huobanplus.search.model.solr.Hot;
import com.huotu.huobanplus.search.utils.PinyinUtils;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;

/**
 * Created by Administrator on 2016/8/22.
 */
public class SolrHotRepository extends SimpleSolrRepository<Hot, String> {

    /**
     * 左匹配搜索
     *
     * @param customerId
     * @param name
     * @param pageSize
     * @return
     */
    public Page<Hot> searchForStartsWith(Long customerId, String name, Integer pageSize) {
        Criteria criteria = new Criteria("id").startsWith(customerId + "_");

        if (PinyinUtils.isChinese(name)) {
            criteria = criteria.and(new Criteria("name").startsWith(name));
        } else {
            String pingyin = PinyinUtils.getFullSpell(name);
            criteria = criteria.and(new Criteria("pinyin").startsWith(pingyin));
        }


        Pageable pageable = new SolrPageRequest(0, pageSize, new Sort(Sort.Direction.DESC, "hot"));
        SimpleQuery query = new SimpleQuery(criteria, pageable);
        return getSolrOperations().queryForPage(query, Hot.class);
    }

    /**
     * 复合搜索 (采用分词)
     *
     * @param customerId
     * @param name
     * @param pageSize
     * @return
     */
    public Page<Hot> searchForComplex(Long customerId, String name, Integer pageSize) {
        Criteria criteria = new Criteria("id").startsWith(customerId + "_");

        if (PinyinUtils.isChinese(name)) {
            criteria = criteria.and(new Criteria("name").is(name));
        } else {
            String pingyin = PinyinUtils.getFullSpell(name);
            criteria = criteria.and(new Criteria("pinyin").is(pingyin));
        }


        Pageable pageable = new SolrPageRequest(0, pageSize, new Sort(Sort.Direction.DESC, "hot"));
        SimpleQuery query = new SimpleQuery(criteria, pageable);
        return getSolrOperations().queryForPage(query, Hot.class);
    }


}
