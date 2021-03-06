package com.huotu.huobanplus.search.service;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.Order;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.OrdersRestRepository;
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
import org.springframework.util.StringUtils;

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
    private OrderService orderService;
    @Autowired
    private GoodsRestRepository goodsRestRepository;
    @Autowired
    private UserRestRepository userRestRepository;
    @Autowired
    private OrdersRestRepository ordersRestRepository;
    @Autowired
    private Environment env;

    public static Long goodsId = 0L;
    public static Long userId = 0L;
    public static String orderId = "";
    //该字段用于单元测试
    public static Long merchantId = null;

    @PostConstruct
    public void init() throws IOException {
        if (env.acceptsProfiles("development")) {
//            merchantId = 4886L;
        }
        Constant.PAGE_SIZE = env.getProperty("com.huotu.huobanplus.search.pageSize", Integer.class, 100);

        /*new Thread(() -> {
            log.info("start goods sync");
            try {
                Thread.sleep(10*1000);
                syncAllGoods();
            } catch (InterruptedException e) {
            }
            log.info("end goods sync");
        }).start();*/
        /*new Thread(() -> {
            log.info("start user sync");
            try {
                Thread.sleep(60 * 1000);
                syncAllUser();
            } catch (InterruptedException e) {
            }
            log.info("end user sync");
        }).start();*/
    }


    private boolean testConnectToHuobanplus(){
        //确保能链接到huoabnplus了再继续往下走
        boolean isSuccess = false;
        int tryNum = 0;
        while (tryNum < 3 && !isSuccess){
            try {
                goodsRestRepository.search(0, null, new PageRequest(0, 1));
                isSuccess = true;
                break;
            }catch (IOException e){
                try {
                    log.info("connect to huobanplus error,wait 10s . . .");
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e1) {
                }
                tryNum ++;
            }
        }
        return isSuccess;
    }


    /**
     * 每天0点5分开始商品全量
     */
    @Scheduled(cron = "0 15 1 * * ?")
    public void syncAllGoods() {
        if(!testConnectToHuobanplus()){
            return;
        }
        log.info("start sync all goods");
        goodsService.updateByCustomerId(merchantId);
        log.info("end sync all goods");
    }

    /**
     * 每小时的第5分钟开始商品增量
     */
    @Scheduled(cron = "0 15 * * * ?")
//    @Scheduled(cron = "0 */2 * * * ?")
    public void addGoods() throws IOException {
        if(!testConnectToHuobanplus()){
            return;
        }
        if (goodsId == null || goodsId == 0) {
            goodsId = goodsService.maxId();
        }
        log.info("set start goods id :" + goodsId);
        log.info("start add goods start with id:" + goodsId);
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        Long maxGoodsId = goodsId;
        while (true) {
            try {
                Page<Goods> goodsPage = goodsRestRepository.search(goodsId, merchantId, new PageRequest(pageNo, pageSize));
                if (goodsPage.getNumberOfElements() == 0) {
                    break;
                }
                goodsService.update(goodsPage.getContent());
                maxGoodsId = goodsPage.getContent().get(goodsPage.getNumberOfElements() - 1).getId();
                Thread.sleep(10);
            } catch (IOException e) {
                log.error("add goods at page " + pageNo + " error : " + e);
                break;
            } catch (InterruptedException e) {
                log.error("sleep error");
                break;
            }
            pageNo++;
        }
        goodsId = maxGoodsId;
        log.info("end add goods end with id:" + goodsId);
    }


    /**
     * 每天0点5分开始用户全量
     */
    @Scheduled(cron = "0 15 1 * * ?")
    public void syncAllUser() {
        if(!testConnectToHuobanplus()){
            return;
        }
        log.info("start sync all user");
        userService.updateByCustomerId(merchantId);
        log.info("end sync all user");
    }

    /**
     * 每小时的第5分钟开始用户增量
     */
    @Scheduled(cron = "0 15 * * * ?")
//    @Scheduled(cron = "0 */5 * * * ?")
    public void addUsers() {
        if(!testConnectToHuobanplus()){
            return;
        }
        if (userId == null || userId == 0) {
            userId = userService.maxId();
        }
        log.info("set start user id : " + userId);
        log.info("start add users start with id:" + userId);
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        Long maxUserId = userId;
        while (true) {
            try {
                Page<User> userPage = userRestRepository.search(userId, merchantId, new PageRequest(pageNo, pageSize));
                if (userPage.getNumberOfElements() == 0) {
                    break;
                }
                userService.update(userPage.getContent());
                maxUserId = userPage.getContent().get(userPage.getNumberOfElements() - 1).getId();
                Thread.sleep(10);
            } catch (IOException e) {
                log.error("add user at page " + pageNo + " error :" + e);
                break;
            } catch (InterruptedException e) {
                log.error("sleep error");
                break;
            }
            pageNo++;
        }
        userId = maxUserId;
        log.info("end add users end with id:" + userId);
    }

    /**
     * 每天0点5分开始订单全量
     */
    @Scheduled(cron = "0 15 1 * * ?")
    public void syncAllOrder() {
        if(!testConnectToHuobanplus()){
            return;
        }
        log.info("start sync all order");
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        while (true) {
            try {
                Page<Order> orderPage = ordersRestRepository.search(0L, merchantId, new PageRequest(pageNo, pageSize));
                if (orderPage.getNumberOfElements() == 0) {
                    break;
                }
                orderService.update(orderPage.getContent());
                Thread.sleep(10);
            } catch (IOException e) {
                log.error("sync order at page " + pageNo + "error : " + e);
                break;
            } catch (InterruptedException e) {
                log.error("sleep error");
                break;
            }
            pageNo++;
            log.debug("sync order pageNo:" + pageNo + " success");
        }
        log.info("end sync all user");
    }

    /**
     * 每小时的第5分钟开始订单增量
     */
    @Scheduled(cron = "0 15 * * * ?")
//    @Scheduled(cron = "0 */2 * * * ?")
    public void addOrder() {
        if(!testConnectToHuobanplus()){
            return;
        }
        if (StringUtils.isEmpty(orderId)) {
            orderId = orderService.maxId();
        }
        log.info("start add order start with id:" + orderId);
        int pageNo = 0, pageSize = Constant.PAGE_SIZE;
        String maxOrderId = orderId;
        while (true) {
            try {
                Page<Order> orderPage = ordersRestRepository.search(orderId, merchantId, new PageRequest(pageNo, pageSize));
                if (orderPage.getNumberOfElements() == 0) {
                    break;
                }
                orderService.update(orderPage.getContent());
                maxOrderId = orderPage.getContent().get(orderPage.getNumberOfElements() - 1).getId();
                Thread.sleep(10);
            } catch (IOException e) {
                log.error("add order at page " + pageNo + " error :" + e);
                break;
            } catch (InterruptedException e) {
                log.error("sleep error");
                break;
            }
            pageNo++;
        }
        orderId = maxOrderId;
        log.info("end add order end with id:" + userId);
    }
}
