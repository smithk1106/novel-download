package com.ideaflow.noveldownload;

import cn.hutool.json.JSONUtil;

import com.ideaflow.noveldownload.entity.AppConfigEntity;
import com.ideaflow.noveldownload.mapper.AppConfigMapper;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NovelDownloadApplicationTests {

    @Resource
    private AppConfigMapper appConfigMapper;

    @Test
    void contextLoads() {
        System.out.println("xxxx");

        String str = "{\"version\":\"1.0\",\"sourceId\":\"\",\"downloadPath\":\"downloads\",\"extName\":\"epub\",\"autoUpdate\":0,\"interactiveMode\":1,\"threads\":-1,\"minInterval\":20,\"maxInterval\":500,\"maxRetryAttempts\":3,\"retryMinInterval\":1000,\"retryMaxInterval\":3000,\"proxyEnabled\":0,\"proxyHost\":\"proxy.example.com\",\"proxyPort\":8080}";
    }

    @Test
    void testAppConfigMapper() {
        AppConfigEntity appConfigEntity = appConfigMapper.selectById(1);

        String configValue = appConfigEntity.getConfigValue();

        AppConfig appConfig = JSONUtil.toBean(appConfigEntity.getConfigValue(), AppConfig.class);
//
        System.out.println(appConfig);
        System.out.println("1");

    }

}
