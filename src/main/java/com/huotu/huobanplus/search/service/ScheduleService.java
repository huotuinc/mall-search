package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.UserRestRepository;
import com.huotu.huobanplus.search.utils.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 商品，订单，用户 每小时一次增量，每天一次全量
 * Created by helloztt on 2017-02-27.
 */
@Service
public class ScheduleService {
    private static final Log log = LogFactory.getLog(ScheduleService.class);
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UserService userService;
    @Autowired
    private GoodsRestRepository goodsRestRepository;
    @Autowired
    private UserRestRepository userRestRepository;
    @Autowired
    private Environment env;

    public static Long goodsId = 0L;
    public static Long userId = 0L;
    //该字段用于单元测试
    public static Long merchantId = null;

    @PostConstruct
    public void init() throws IOException {
        if (env.acceptsProfiles("development")) {
            merchantId = 4886L;
        }
        Constant.PAGE_SIZE = env.getProperty("com.huotu.huobanplus.search.pageSize",Integer.class,100);
    }


    /**
     * 每天0点5分开始商品全量
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void syncAllGoods() {
        log.info("start sync all goods");
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            try {
                Page<Goods> goodsPage = goodsRestRepository.search(0L, merchantId, new PageRequest(pageNo, pageSize));
                if (goodsPage.getNumberOfElements() == 0) {
                    break;
                }
                goodsService.update(goodsPage.getContent());
            } catch (IOException e) {
                log.error("sync goods at page " + pageNo + " error : " + e);
            }
            pageNo++;
        }
        log.info("end sync all goods");
    }

    /**
     * 每小时的第5分钟开始商品增量
     */
    @Scheduled(cron = "0 5 * * * ?")
//    @Scheduled(cron = "0 */5 * * * ?")
    public void addGoods() throws IOException {
        goodsId = goodsService.maxId();
        log.info("set start goods id :" + goodsId);
        log.info("start add goods start with id:" + goodsId);
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            try {
                Page<Goods> goodsPage = goodsRestRepository.search(goodsId, merchantId, new PageRequest(pageNo, pageSize));
                if (goodsPage.getNumberOfElements() == 0) {
                    break;
                }
                goodsService.update(goodsPage.getContent());
            } catch (IOException e) {
                log.error("add goods at page " + pageNo + " error : " +e);
            }
            pageNo++;
        }
        log.info("end add goods end with id:" + goodsId);
    }


    /**
     * 每天0点5分开始商品全量
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void syncAllUser() {
        log.info("start sync all user");
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            try {
                Page<User> userPage = userRestRepository.search(0L, merchantId, new PageRequest(pageNo, pageSize));
                if (userPage.getNumberOfElements() == 0) {
                    break;
                }
                userService.update(userPage.getContent());
            } catch (IOException e) {
                log.error("sync user at page " + pageNo + "error : "+ e);
            }
            pageNo++;
        }
        log.info("end sync all user");
    }

    /**
     * 每小时的第5分钟开始用户增量
     */
    @Scheduled(cron = "0 5 * * * ?")
//    @Scheduled(cron = "0 */5 * * * ?")
    public void addUsers() {
//        if (userId == null || userId == 0) {
            userId = userService.maxId();
//        }
        log.info("set start user id : " + userId);
        log.info("start add users start with id:" + userId);
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            try {
                Page<User> userPage = userRestRepository.search(userId, merchantId, new PageRequest(pageNo, pageSize));
                if (userPage.getNumberOfElements() == 0) {
                    break;
                }
                userService.update(userPage.getContent());
            } catch (IOException e) {
                log.error("add user at page " + pageNo + " error :" + e);
            }
            pageNo++;
        }
        log.info("end add users end with id:" + userId);
    }
}
