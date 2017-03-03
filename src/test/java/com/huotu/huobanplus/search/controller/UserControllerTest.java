package com.huotu.huobanplus.search.controller;

import com.huotu.huobanplus.search.BaseTest;
import com.huotu.huobanplus.search.model.solr.User;
import com.huotu.huobanplus.search.repository.solr.SolrUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by helloztt on 2017-02-28.
 */
public class UserControllerTest extends BaseTest {
    @Autowired
    private SolrUserRepository solrUserRepository;

    private String baseUrl = "/user";
    private User mockUser;

    @Before
    public void init() {
    }

    @Test
    public void testSearch() throws Exception {
        solrUserRepository.deleteAll();
        mockUser = mockUser();
        mockUser.setDiyTagIds("|1|2|3|");
        solrUserRepository.save(mockUser);
        String controllerUrl = baseUrl + "/search";
        //根据会员类型搜索
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(1 - mockUser.getUserType())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();
        //根据会员等级搜索
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("levelId", String.valueOf(mockUser.getLevelId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("levelId", String.valueOf(1 + mockUser.getLevelId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();

        //是否绑定手机
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("levelId", mockUser.isMobileBindRequired() ? "-102" : "-103"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("mobileBindRequired", String.valueOf(!mockUser.isMobileBindRequired())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();

        //根据登录名搜索（精准搜索）
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "1")
                .param("searchValue", mockUser.getLoginName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "1")
                .param("searchValue", mockUser.getLoginName().substring(mockUser.getLoginName().length() / 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();

        //根据从属小伙伴登录名搜索（精准搜索）
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "3")
                .param("searchValue", mockUser.getParentLoginName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "3")
                .param("searchValue", mockUser.getParentLoginName().substring(mockUser.getParentLoginName().length() / 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();

        //根据openId搜索（精准搜索）
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "5")
                .param("searchValue", mockUser.getOpenId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "5")
                .param("searchValue", mockUser.getOpenId().substring(mockUser.getOpenId().length() / 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();

        //根据姓名搜索（模糊搜索）
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "2")
                .param("searchValue", mockUser.getRealName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "2")
                .param("searchValue", mockUser.getRealName().substring(mockUser.getRealName().length() / 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        //根据昵称搜索（模糊搜索）
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "4")
                .param("searchValue", mockUser.getNickName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("searchType", "4")
                .param("searchValue", mockUser.getNickName().substring(mockUser.getNickName().length() / 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        //会员标签搜索
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("diyTags", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("diyTags", "1|3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("diyTags", "1|5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("diyTags", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();

        //积分区间搜索
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("minIntegral", String.valueOf(mockUser.getUserIntegral() - 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("minIntegral", String.valueOf(mockUser.getUserIntegral())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("minIntegral", String.valueOf(mockUser.getUserIntegral() + 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("maxIntegral", String.valueOf(mockUser.getUserIntegral() - 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("maxIntegral", String.valueOf(mockUser.getUserIntegral())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("maxIntegral", String.valueOf(mockUser.getUserIntegral() + 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();

        //注册时间搜索
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar regTime = Calendar.getInstance();
        regTime.setTime(mockUser.getRegTime());
        String regTimeStr = sf.format(regTime.getTime());
        regTime.set(Calendar.DATE, regTime.get(Calendar.DATE) - 1);
        String beforeRegTime = sf.format(regTime.getTime());
        regTime.set(Calendar.DATE, regTime.get(Calendar.DATE) + 2);
        String afterRegTime = sf.format(regTime.getTime());
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("txtBeginTime", beforeRegTime))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("txtBeginTime", regTimeStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("txtBeginTime", afterRegTime))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();

        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("txtEndTime", beforeRegTime))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(0))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("txtEndTime", regTimeStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("userType", String.valueOf(mockUser.getUserType()))
                .param("txtEndTime", afterRegTime))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(1))
                .andReturn();
    }

    @Test
    public void testSort() throws Exception {
        String controllerUrl = baseUrl + "/search";
        solrUserRepository.deleteAll();
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userList.add(mockUser());
        }
        solrUserRepository.save(userList);

        //默认根据注册时间降序
        userList.sort((user1,user2)->user2.getRegTime().compareTo(user1.getRegTime()));
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(userList.size()))
                .andExpect(jsonPath("$.ids[0]").value(userList.get(0).getId().intValue()))
                .andExpect(jsonPath("$.ids[" + (userList.size() - 1)+ "]").value(userList.get(userList.size() - 1).getId().intValue()))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("sortType", "0")
                .param("sortDir", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(userList.size()))
                .andExpect(jsonPath("$.ids[0]").value(userList.get(0).getId().intValue()))
                .andExpect(jsonPath("$.ids[" + (userList.size() - 1)+ "]").value(userList.get(userList.size() - 1).getId().intValue()))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("sortType", "999")
                .param("sortDir", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(userList.size()))
                .andExpect(jsonPath("$.ids[0]").value(userList.get(0).getId().intValue()))
                .andExpect(jsonPath("$.ids[" + (userList.size() - 1)+ "]").value(userList.get(userList.size() - 1).getId().intValue()))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("sortType", "0")
                .param("sortDir", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(userList.size()))
                .andExpect(jsonPath("$.ids[0]").value(userList.get(userList.size() - 1).getId().intValue()))
                .andExpect(jsonPath("$.ids[" + (userList.size() - 1)+ "]").value(userList.get(0).getId().intValue()))
                .andReturn();

        //根据积分排序
        userList.sort((user1,user2)->user2.getUserIntegral().compareTo(user1.getUserIntegral()));
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("sortType", "1")
                .param("sortDir", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(userList.size()))
                .andExpect(jsonPath("$.ids[0]").value(userList.get(0).getId().intValue()))
                .andExpect(jsonPath("$.ids[" + (userList.size() - 1)+ "]").value(userList.get(userList.size() - 1).getId().intValue()))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("sortType", "1")
                .param("sortDir", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(userList.size()))
                .andExpect(jsonPath("$.ids[0]").value(userList.get(userList.size() - 1).getId().intValue()))
                .andExpect(jsonPath("$.ids[" + (userList.size() - 1)+ "]").value(userList.get(0).getId().intValue()))
                .andReturn();

        //根据余额排序
        userList.sort((user1,user2)->user2.getUserBalance().compareTo(user1.getUserBalance()));
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("sortType", "2")
                .param("sortDir", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(userList.size()))
                .andExpect(jsonPath("$.ids[0]").value(userList.get(0).getId().intValue()))
                .andExpect(jsonPath("$.ids[" + (userList.size() - 1)+ "]").value(userList.get(userList.size() - 1).getId().intValue()))
                .andReturn();
        mockMvc.perform(post(controllerUrl)
                .param("customerId", String.valueOf(customerId))
                .param("sortType", "2")
                .param("sortDir", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.recordCount").value(userList.size()))
                .andExpect(jsonPath("$.ids[0]").value(userList.get(userList.size() - 1).getId().intValue()))
                .andExpect(jsonPath("$.ids[" + (userList.size() - 1)+ "]").value(userList.get(0).getId().intValue()))
                .andReturn();
    }

}