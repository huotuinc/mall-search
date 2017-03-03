package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.UserRestRepository;
import com.huotu.huobanplus.search.utils.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    //该字段用于单元测试
//    public static Long merchantId = null;
    public static Long merchantId = 4886L;

    public static Long goodsId = 0L;
    public static Long userId = 0L;

    @PostConstruct
    public void init() throws IOException {
        goodsId = goodsService.maxId();
        log.info("set start goods id :" + goodsId);
        userId = userService.maxId();
        log.info("set start user id : " + userId);
    }


    /**
     * 每天0点5分开始商品全量
     */
    @Scheduled(cron = "0 0 0 * * ?")
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
                pageNo++;
            } catch (IOException e) {
                log.error(e);
                break;
            }
        }
        log.info("end sync all goods");
    }

    /**
     * 每小时的第5分钟开始商品增量
     */
    @Scheduled(cron = "0 5 * * * ?")
//    @Scheduled(cron = "0 */5 * * * ?")
    public void addGoods() {
        log.info("start add goods start with id:" + goodsId);
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            try {
                Page<Goods> goodsPage = goodsRestRepository.search(goodsId, merchantId, new PageRequest(pageNo, pageSize));
                if (goodsPage.getNumberOfElements() == 0) {
                    break;
                }
                goodsService.update(goodsPage.getContent());
                goodsId = goodsPage.getContent().get(goodsPage.getNumberOfElements() - 1).getId();
            } catch (IOException e) {
                log.error(e);
                break;
            }
        }
        log.info("end add goods end with id:" + goodsId);
    }


    /**
     * 每天0点15分开始商品全量
     */
    @Scheduled(cron = "0 0 0 * * ?")
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
                pageNo++;
            } catch (IOException e) {
                log.error(e);
                break;
            }
        }
        log.info("end sync all user");
    }

    /**
     * 每小时的第5分钟开始用户增量
     */
//    @Scheduled(cron = "0 5 * * * ?")
    @Scheduled(cron = "0 */5 * * * ?")
    public void addUsers() {
        log.info("start add users start with id:" + goodsId);
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            try {
                Page<User> userPage = userRestRepository.search(userId, merchantId, new PageRequest(pageNo, pageSize));
                if (userPage.getNumberOfElements() == 0) {
                    break;
                }
                userService.update(userPage.getContent());
                userId = userPage.getContent().get(userPage.getNumberOfElements() - 1).getId();
            } catch (IOException e) {
                log.error(e);
                break;
            }
        }
        log.info("end add users end with id:" + userId);
    }
}
