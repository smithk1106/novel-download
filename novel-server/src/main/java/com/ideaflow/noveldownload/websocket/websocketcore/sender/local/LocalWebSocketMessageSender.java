package com.ideaflow.noveldownload.websocket.websocketcore.sender.local;


import com.ideaflow.noveldownload.websocket.websocketcore.sender.AbstractWebSocketMessageSender;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;
import com.ideaflow.noveldownload.websocket.websocketcore.session.WebSocketSessionManager;

/**
 * 本地的 {@link WebSocketMessageSender} 实现类
 *
 * 注意：仅仅适合单机场景！！！
 */
public class LocalWebSocketMessageSender extends AbstractWebSocketMessageSender {

    public LocalWebSocketMessageSender(WebSocketSessionManager sessionManager) {
        super(sessionManager);
    }

}
