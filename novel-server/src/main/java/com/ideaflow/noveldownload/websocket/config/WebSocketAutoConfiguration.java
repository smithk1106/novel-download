package com.ideaflow.noveldownload.websocket.config;

import com.ideaflow.noveldownload.websocket.websocketcore.handler.JsonWebSocketMessageHandler;
import com.ideaflow.noveldownload.websocket.websocketcore.listener.WebSocketMessageListener;
import com.ideaflow.noveldownload.websocket.websocketcore.security.LoginUserHandshakeInterceptor;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.local.LocalWebSocketMessageSender;
import com.ideaflow.noveldownload.websocket.websocketcore.session.WebSocketSessionHandlerDecorator;
import com.ideaflow.noveldownload.websocket.websocketcore.session.WebSocketSessionManager;
import com.ideaflow.noveldownload.websocket.websocketcore.session.WebSocketSessionManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;

@Configuration // 让这个类 自动装配
@EnableWebSocket // 开启 websocket
@ConditionalOnProperty(prefix = "ideaflow.websocket", value = "enable", matchIfMissing = true)
@EnableConfigurationProperties(WebSocketProperties.class)
@Slf4j
public class WebSocketAutoConfiguration {

    @Bean
    public WebSocketConfigurer webSocketConfigurer(HandshakeInterceptor[] handshakeInterceptors,
                                                   WebSocketHandler webSocketHandler,
                                                   WebSocketProperties webSocketProperties) {
        return registry -> registry
                // 第一步:添加 WebSocketHandler
                .addHandler(webSocketHandler, webSocketProperties.getPath())
                // 第二步: 添加拦截器 用于认证等
                .addInterceptors(handshakeInterceptors)
                // 允许跨域，否则前端连接会直接断开
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new LoginUserHandshakeInterceptor();
    }

    /**
     * 定义需要处理的消息
     * @param sessionManager
     * @param messageListeners
     * @return
     */
    @Bean
    public WebSocketHandler webSocketHandler(WebSocketSessionManager sessionManager,
                                             List<? extends WebSocketMessageListener<?>> messageListeners) {
        // 1. 创建 JsonWebSocketMessageHandler 对象，处理消息
        JsonWebSocketMessageHandler messageHandler = new JsonWebSocketMessageHandler(messageListeners);
        // 2. 创建 WebSocketSessionHandlerDecorator 对象，处理连接
        return new WebSocketSessionHandlerDecorator(messageHandler, sessionManager);
    }

    /**
     *
     * @return
     */
    @Bean
    public WebSocketSessionManager webSocketSessionManager() {
        return new WebSocketSessionManagerImpl();
    }

    // ==================== Sender 相关 ====================

    @Configuration
    @ConditionalOnProperty(prefix = "wpl.websocket", name = "sender-type", havingValue = "local", matchIfMissing = true)
    public class LocalWebSocketMessageSenderConfiguration {

        @Bean
        public LocalWebSocketMessageSender localWebSocketMessageSender(WebSocketSessionManager sessionManager) {
            return new LocalWebSocketMessageSender(sessionManager);
        }

    }


}