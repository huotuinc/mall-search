package com.huotu.huobanplus.search.boot;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Administrator on 2016/8/20.
 */
@Configuration
@EnableWebMvc
@EnableScheduling
public class MvcConfig extends WebMvcConfigurerAdapter {
}
