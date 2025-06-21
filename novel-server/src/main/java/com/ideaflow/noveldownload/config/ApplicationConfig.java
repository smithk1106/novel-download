package com.ideaflow.noveldownload.config;


import com.ideaflow.noveldownload.mapper.SearchResultMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author wangpenglong
 * @Date 2024/9/3
 * @Description
 */
@Slf4j
@Configuration
public class ApplicationConfig implements  InitializingBean {

    @Resource
    private SearchResultMapper searchResultMapper;
    @Override
    public void afterPropertiesSet() throws Exception {
        int delete = searchResultMapper.delete(null);
        log.info("searchResultMapper-删除数据:"+delete);
    }
}
