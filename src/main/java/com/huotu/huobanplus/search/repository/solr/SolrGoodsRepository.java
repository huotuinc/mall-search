package com.huotu.huobanplus.search.repository.solr;

import com.huotu.huobanplus.search.model.solr.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.util.StringUtils;

/**
 * Created by Administrator on 2016/8/17.
 */
public class SolrGoodsRepository extends SimpleSolrRepository<Goods, Long> {

    public Page<Goods> search(Long customerId, Integer pageSize, Integer pageNo
            , String key, String brandIds, String categoryIds, String tagIds, Integer sorts) {
        Criteria criteria = new Criteria("customerId").is(customerId);

        //模糊搜索字段权重：商品名称，关键字，品牌，分类名称，副标题，热点名称。
        if (!StringUtils.isEmpty(key)) {
            String[] keys = key.split(" ");
            Criteria keyCriteria = new Criteria("title").is(keys).boost(1.0f)
                    .or(new Criteria("keyword").is(keys).boost(0.9f))
                    .or(new Criteria("brandName").is(keys).boost(0.8f))
                    .or(new Criteria("categoryName").is(keys).boost(0.7f))
                    .or(new Criteria("virtualCategory").is(keys).boost(0.6f))
                    .or(new Criteria("tags").is(keys).boost(0.5f))
                    .or(new Criteria("description").is(keys).boost(0.4f))
                    .or(new Criteria("hotspot").is(keys).boost(0.3f));
            criteria = criteria.and(keyCriteria);
        }
        //根据品牌ID搜索
        if (!StringUtils.isEmpty(brandIds)) {
            criteria = criteria.and(new Criteria("brandId").is(brandIds.split("\\|")));
        }
        //根据分类ID搜索
        if (!StringUtils.isEmpty(categoryIds)) {
            criteria = criteria.and(new Criteria("categoriesId").contains(categoryIds.split("\\|")));
        }
        //根据标签ID搜索（1商品-N标签）
        if (!StringUtils.isEmpty(tagIds)) {
            criteria = criteria.and(new Criteria("tagIds").contains(tagIds.split("\\|")));
        }

        Pageable pageable = new SolrPageRequest(pageNo, pageSize, getSortBySortId(sorts));
        SimpleQuery query = new SimpleQuery(criteria, pageable);
        return getSolrOperations().queryForPage(query, Goods.class);
    }

    /**
     * 排序规则：第一位表示排序字段，第二位表示升降序（0：升，1：降）
     * 0（默认）代表 销量（降序），价格（降序），上架时间（降序）
     * 10代表 上架时间升序 11代表 上架时间降序
     * 21代表 销量降序
     * 30代表 价格升序 31价格降序
     * @param sortId
     * @return
     */
    private Sort getSortBySortId(Integer sortId) {
        if(sortId == null){
            return null;
        }
        String sortColumnName = null;
        Sort.Direction sortDirect = Sort.Direction.DESC;
        Sort sort;
        if(sortId / 10 > 0){
            switch (sortId / 10){
                case 1:
                    sortColumnName = "updateTime";break;
                case 2:
                    sortColumnName = "sales";break;
                case 3:
                    sortColumnName = "originalPrice";break;
            }
            switch (sortId % 10){
                case 0:
                    sortDirect = Sort.Direction.ASC;break;
                case 1:
                    sortDirect = Sort.Direction.DESC;break;
            }
        }
        if(StringUtils.isEmpty(sortColumnName)){
            sort = new Sort(Sort.Direction.DESC, "sales", "originalPrice", "updateTime");
        }else{
            sort = new Sort(sortDirect,sortColumnName);
        }
        return sort;
    }

    private Long[] stringArrToLongArr(String[] arrStr) {
        Long[] arrLong = new Long[arrStr.length];
        for (int i = 0; i < arrStr.length; i++) {
            arrLong[i] = Long.parseLong(arrStr[i]);
        }
        return arrLong;
    }

    private String[] stringArrWithSeparator(String[] arrStr) {
        for (String arr : arrStr) {
            arr = "|" + arr + "|";
        }
        return arrStr;
    }

}
