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
        if(userPage.getNumberOfElements() > 0){
            Long[] ids = new Long[userPage.getNumberOfElements()];
            for (int i = 0; i < userPage.getNumberOfElements(); i++) {
                ids[i] = userPage.getContent().get(i).getId();
            }
            viewGoodsList.setIds(ids);
        }
        return viewGoodsList;
    }

    @Override
    public Long maxId() {
        return solrUserRepository.searchMaxId();
    }

    @Override
    public void update(Long userId) throws IOException {
        User solrUser = solrUserRepository.findOne(userId);
        solrUser = mallUserToSolrUser(userId, solrUser);
        if (solrUser != null) {
            solrUserRepository.save(solrUser);
        }
    }

    @Override
    public void updateByCustomerId(Long customerId) {
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        long userId = 0L;
        while (true) {
            try {
                long start = System.currentTimeMillis();
                //如果按照分页一页一页查询，越后面的页码会越查越慢，所以设置起始的userId，每次只查第一页，这样会提高查询效率
                Page<com.huotu.huobanplus.common.entity.User> mallUserList = userRestRepository.search(userId,customerId, new PageRequest(pageNo, pageSize));
                long end = System.currentTimeMillis();
                if (mallUserList.getNumberOfElements() == 0) {
                    break;
                }
                update(mallUserList.getContent());
                long update = System.currentTimeMillis();
                Thread.sleep(10);
                log.debug("search page last " + (end - start) + " ms," +
                        "update data last " + (update - end) + " ms");
                userId = mallUserList.getContent().get(mallUserList.getNumberOfElements() - 1).getId();
            } catch (IOException e) {
                log.error("sync users start id " + userId + ", pageNo:" + pageNo + " error");
                //为了防止因为某个ID的查询失败而无法同步后面的数据，在此跳过这个ID
                userId ++;
            } catch (InterruptedException e) {
                log.error("sleep error");
            }
//            pageNo++;
            log.debug("sync users start id " + userId + ", pageNo:" + pageNo + " success");
        }
    }

    /*@Override
    public void update(com.huotu.huobanplus.common.entity.User mallUser) throws IOException {
        User solrUser = solrUserRepository.findOne(mallUser.getId());
        solrUser = mallUserToSolrUser(mallUser, solrUser);
        if (solrUser != null) {
            solrUserRepository.save(solrUser);
        }

    }*/

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
            solrUser.setCustomerId(mallUser.getMerchantId());
        }
        solrUser.setLevelId(mallUser.getLevelId());
        if (mallUser.getUserType() != null) {
            solrUser.setUserType(mallUser.getUserType().ordinal());
        }
        solrUser.setLoginName(mallUser.getLoginName());
        solrUser.setParentLoginName(mallUser.getParentLoginName());
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
        if(!StringUtils.isEmpty(mallUser.getOpenId())){
            solrUser.setOpenId(mallUser.getOpenId());
        }else{
            solrUser.setOpenId(null);
        }
        List<UserDiyTag> diyTagList = mallUser.getDiyTagList();
        if (diyTagList != null && diyTagList.size() > 0) {
            StringBuilder diyTagSb = new StringBuilder("|");
            for (UserDiyTag tag : diyTagList) {
                diyTagSb.append(tag.getTagId()).append("|");
            }
            solrUser.setDiyTagIds(diyTagSb.toString());
        }else{
            solrUser.setDiyTagIds(null);
        }
        return solrUser;
    }

    /**
     * 根据userId查询MallUser,由于各种关联会产生很多不必要的查询，所以查询效率低，只适用于某个id的用户更新
     * @param userId
     * @param solrUser
     * @return
     * @throws IOException
     */
    private User mallUserToSolrUser(Long userId, User solrUser) throws IOException {
        com.huotu.huobanplus.common.entity.User mallUser = userRestRepository.getOneByPK(userId);
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
