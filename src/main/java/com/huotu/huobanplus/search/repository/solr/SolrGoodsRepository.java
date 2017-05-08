package com.huotu.huobanplus.search.repository.solr;

import com.huotu.huobanplus.search.model.solr.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/17.
 */
public class SolrGoodsRepository extends SimpleSolrRepository<Goods, Long> {

    public Long searchMaxId() {
        //因为主键只能保存为string类型，查询最大值不准确，所以用时间替代
        Pageable pageable = new SolrPageRequest(0, 1, new Sort(Sort.Direction.DESC, "updateTime"));
        SimpleQuery query = new SimpleQuery("*:*", pageable);
        Page<Goods> maxGoods = getSolrOperations().queryForPage(query, Goods.class);
        if (maxGoods.getNumberOfElements() == 0) {
            return 0L;
        } else {
            return maxGoods.getContent().get(0).getId();
        }
    }

    public Page<Goods> search(Long customerId, Long supplierId, Long ownId, Integer pageSize, Integer pageNo
            , String key, String brandIds, String categoryIds, String typeIds, String tagIds, String sorts) {
        Criteria criteria = new Criteria("customerId").is(customerId)
                .and(new Criteria("disabled").is(false));
        if (ownId != -1) {
            criteria = criteria.and(new Criteria("ownerId").is(ownId));
        }
        if (supplierId != null && supplierId != -1) {
            criteria = criteria.and(new Criteria("supplierId").is(supplierId));
        }

        //模糊搜索字段权重：商品名称，关键字，品牌，分类名称，副标题，热点名称。
        if (!StringUtils.isEmpty(key)) {
            Criteria keyCriteria = new Criteria("title").is(key).boost(1.0f)
                    .or(new Criteria("keyword").is(key).boost(0.9f))
                    .or(new Criteria("brandName").is(key).boost(0.8f))
                    .or(new Criteria("categoryName").is(key).boost(0.7f))
                    .or(new Criteria("virtualCategory").is(key).boost(0.6f))
                    .or(new Criteria("tags").is(key).boost(0.5f))
                    .or(new Criteria("description").is(key).boost(0.4f))
                    .or(new Criteria("hotspot").is(key).boost(0.3f));
            criteria = criteria.and(keyCriteria);
        }
        //根据品牌ID搜索
        if (!StringUtils.isEmpty(brandIds)) {
            criteria = criteria.and(new Criteria("brandId").is(brandIds));
        }
        //根据分类ID搜索
        if (!StringUtils.isEmpty(categoryIds)) {
            criteria = criteria.and(new Criteria("categoriesId").is(categoryIds));
        }
        //根据类目ID搜索
        if (!StringUtils.isEmpty(typeIds)) {
            criteria = criteria.and(new Criteria("typePath").is(typeIds));
        }
        //根据标签ID搜索（1商品-N标签）
        if (!StringUtils.isEmpty(tagIds)) {
            criteria = criteria.and(new Criteria("tagIds").is(tagIds));
        }

        Pageable pageable = new SolrPageRequest(pageNo, pageSize, getSortBySortId(sorts));
        SimpleQuery query = new SimpleQuery(criteria, pageable);
        return getSolrOperations().queryForPage(query, Goods.class);
    }

    /**
     * 排序规则：
     *
     *              按时间降序排序 = 1,
     *              按时间升序排序 = 2,
     *              按销售量降序排序 = 3,
     *              按销售量升序排序 = 4,
     *              按价格降序排序 = 5,
     *              按价格升序排序 = 6
     *
     * @param sortId
     * @return
     */
    private Sort getSortBySortId(String sortId) {
        if (StringUtils.isEmpty(sortId)) {
            return null;
        }
        Sort sort = null;
        String[] sortTypeStr = sortId.split("|");
        List<Sort.Order> orderList = new ArrayList<>();
        for(String sortType : sortTypeStr){
            switch (sortType){
                case "1":
                    orderList.add(new Sort.Order(Sort.Direction.DESC,"updateTime"));
                    break;
                case "2":
                    orderList.add(new Sort.Order(Sort.Direction.ASC,"updateTime"));
                    break;
                case "3":
                    orderList.add(new Sort.Order(Sort.Direction.DESC,"sales"));
                    break;
                case "4":
                    orderList.add(new Sort.Order(Sort.Direction.ASC,"sales"));
                    break;
                case "5":
                    orderList.add(new Sort.Order(Sort.Direction.DESC,"minUserPrice"));
                    break;
                case "6":
                    orderList.add(new Sort.Order(Sort.Direction.ASC,"minUserPrice"));
                    break;
            }
        }
        if(orderList != null && orderList.size() > 0){
            sort = new Sort(orderList);
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
