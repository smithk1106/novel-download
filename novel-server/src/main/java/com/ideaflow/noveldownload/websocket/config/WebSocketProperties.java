package com.ideaflow.noveldownload.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("ideaflow.websocket")
@Data
public class WebSocketProperties {

    /**
     * WebSocket 的连接路径
     */
    private String path = "/ws";

    /**
     * 消息发送器的类型
     *
     * 可选值：local、redis、rocketmq、kafka、rabbitmq
     */
    private String senderType = "local";

}
