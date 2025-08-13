package com.ideaflow.noveldownload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;


@Data
@Component
@ConfigurationProperties(prefix = "spring.app")
public class AppProperties {
    private String contentBase;
}
