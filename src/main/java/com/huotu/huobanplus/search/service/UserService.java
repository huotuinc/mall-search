package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.search.model.view.ViewList;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2017-02-28.
 */
public interface UserService {
    ViewList search(Long customerId, Integer pageSize, Integer pageNo
            , Integer levelId, Integer userType
            , String searchColumn, String fuzzySearchColumn, String fuzzySearchValue
            , Integer minIntegral, Integer maxIntegral
            , Date searchBeginTime, Date searchEndTime
            , Boolean mobileBindRequired, String diyTags
            , String sortColumn, Sort.Direction sortDirect);

    Long maxId();

    void update(Long userId) throws IOException;

    void updateByCustomerId(Long customerId);

//    void update(User user) throws IOException;

    void update(List<User> userList) throws IOException;

}
