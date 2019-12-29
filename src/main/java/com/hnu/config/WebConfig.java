package com.hnu.config;

import com.hnu.access.AccssIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    UserArgumentResolvers resolvers;

    @Autowired
    AccssIntercepter accssIntercepter;

    //框架最后会回到这个方法，看看controller方法中的方法参数有没有某个值，有就给他注进去。
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(resolvers);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //将自己写的拦截器注册进来
       registry.addInterceptor(accssIntercepter);
    }
}
