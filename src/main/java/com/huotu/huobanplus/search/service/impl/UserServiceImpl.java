package com.huotu.huobanplus.search.service.impl;

import com.huotu.huobanplus.common.entity.UserDiyTag;
import com.huotu.huobanplus.sdk.common.repository.UserDiyTagRestRepository;
import com.huotu.huobanplus.sdk.common.repository.UserRestRepository;
import com.huotu.huobanplus.search.model.solr.User;
import com.huotu.huobanplus.search.model.view.ViewList;
import com.huotu.huobanplus.search.repository.solr.SolrUserRepository;
import com.huotu.huobanplus.search.service.UserService;
import com.huotu.huobanplus.search.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2017-02-28.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Log log = LogFactory.getLog(UserServiceImpl.class);
    @Autowired
    private SolrUserRepository solrUserRepository;
    @Autowired
    private UserRestRepository userRestRepository;
    @Autowired
    private UserDiyTagRestRepository diyTagRestRepository;

    @Override
    public ViewList search(Long customerId, Integer pageSize, Integer pageNo
            , Integer levelId, Integer userType
            , String searchColumn, String fuzzySearchColumn, String searchValue
            , Integer minIntegral, Integer maxIntegral
            , Date searchBeginTime, Date searchEndTime
            , Boolean mobileBindRequired, String diyTags
            , String sortColumn, Sort.Direction sortDirect) {
        Page<User> userPage = solrUserRepository.search(customerId, pageSize, pageNo, levelId, userType
                , searchColumn, fuzzySearchColumn, searchValue
                , minIntegral, maxIntegral, searchBeginTime, searchEndTime
                , mobileBindRequired, diyTags, sortColumn, sortDirect);

        ViewList viewGoodsList = new ViewList();
        viewGoodsList.setPageSize(pageSize);
        viewGoodsList.setPage(pageNo);
        viewGoodsList.setRecordCount(userPage.getTotalElements());
        Long[] ids = new Long[userPage.getNumberOfElements()];
        for (int i = 0; i < userPage.getNumberOfElements(); i++) {
            ids[i] = userPage.getContent().get(i).getId();
        }
        viewGoodsList.setIds(ids);

        return viewGoodsList;
    }

    @Override
    public Long maxId() {
        return solrUserRepository.searchMaxId();
    }

    @Override
    public void update(Long userId) throws IOException {
        com.huotu.huobanplus.common.entity.User mallUser = userRestRepository.getOneByPK(userId);
        if (mallUser != null) {
            update(mallUser);
        }
    }

    @Override
    public void updateByCustomerId(Long customerId) throws IOException {
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            Page<com.huotu.huobanplus.common.entity.User> mallUserList = userRestRepository.findByMerchantId(customerId, new PageRequest(pageNo, pageSize));
            if (mallUserList.getNumberOfElements() == 0) {
                break;
            }
            update(mallUserList.getContent());
            pageNo++;
        }
    }

    @Override
    public void update(com.huotu.huobanplus.common.entity.User mallUser) throws IOException {
        User solrUser = solrUserRepository.findOne(mallUser.getId());
        solrUser = mallUserToSolrUser(mallUser, solrUser);
        if (solrUser != null) {
            solrUserRepository.save(solrUser);
        }

    }

    @Override
    public void update(List<com.huotu.huobanplus.common.entity.User> userList) throws IOException {
        if (userList == null || userList.size() == 0) {
            return;
        }
        List<User> solrUserList = new ArrayList<>();
        userList.forEach(mallUser -> {
            User solrUser = solrUserRepository.findOne(mallUser.getId());
            try {
                solrUser = mallUserToSolrUser(mallUser, solrUser);
            } catch (IOException e) {
                log.error("userId:" + mallUser.getId() + "," + e);
//                System.out.println(e);
            }
            if (solrUser != null) {
                solrUserList.add(solrUser);
            }
        });
        solrUserRepository.save(solrUserList);
    }

    private User mallUserToSolrUser(com.huotu.huobanplus.common.entity.User mallUser, User solrUser) throws IOException {
        if (mallUser == null) {
            return null;
        }
        if (solrUser == null) {
            solrUser = new User();
            solrUser.setId(mallUser.getId());
        }
        if (mallUser.getMerchant() != null) {
            solrUser.setCustomerId(mallUser.getMerchant().getId());
        }
        solrUser.setLevelId(mallUser.getLevelId());
        if (mallUser.getUserType() != null) {
            solrUser.setUserType(mallUser.getUserType().ordinal());
        }
        solrUser.setLoginName(mallUser.getLoginName());
        com.huotu.huobanplus.common.entity.User parentUser = null;
        if (mallUser.getBelongOne() != null && mallUser.getBelongOne() > 0) {
            parentUser = userRestRepository.getOneByPK(mallUser.getBelongOne());
        }
        if (parentUser != null) {
            solrUser.setParentLoginName(parentUser.getLoginName());
        }
        solrUser.setMobileBindRequired(mallUser.isMobileBindRequired());
        if (StringUtils.isNotEmpty(mallUser.getWxNickName())) {
            solrUser.setNickName(mallUser.getWxNickName());
        } else {
            solrUser.setNickName(mallUser.getLoginName());
        }
        solrUser.setRealName(mallUser.getRealName());
        if (mallUser.getUserBalance() != null && mallUser.getLockedBalance() != null) {
            solrUser.setUserBalance(new BigDecimal(mallUser.getUserBalance() - mallUser.getLockedBalance()).setScale(2, RoundingMode.HALF_DOWN).doubleValue());
        } else {
            solrUser.setUserBalance(mallUser.getUserBalance());
        }
        if (mallUser.getUserIntegral() != null && mallUser.getLockedIntegral() != null) {
            solrUser.setUserIntegral(mallUser.getUserIntegral() - mallUser.getLockedIntegral().longValue());
        } else {
            solrUser.setUserIntegral(mallUser.getUserIntegral());
        }
        solrUser.setRegTime(mallUser.getRegTime());
        solrUser.setDeleted(mallUser.isDeleted());
        if (mallUser.getBinding() != null) {
            solrUser.setOpenId(mallUser.getBinding().getOpenId());
        }
        List<UserDiyTag> diyTagList = diyTagRestRepository.findByUserId(mallUser.getId());
        if (diyTagList != null && diyTagList.size() > 0) {
            StringBuilder diyTagSb = new StringBuilder("|");
            for (UserDiyTag tag : diyTagList) {
                diyTagSb.append(tag.getTagId()).append("|");
            }
            solrUser.setDiyTagIds(diyTagSb.toString());
        }
        return solrUser;
    }
}
