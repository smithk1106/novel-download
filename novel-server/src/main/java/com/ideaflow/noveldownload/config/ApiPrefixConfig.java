package com.ideaflow.noveldownload.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 给所有 @RestController 的请求添加前缀 /api
        configurer.addPathPrefix("/api", 
            c -> c.isAnnotationPresent(RestController.class));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 匹配以 pages 开头的路径并转发到 index.html
        registry.addViewController("/pages/**").setViewName("forward:/index.html");
    }

}