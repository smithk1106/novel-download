package com.ideaflow.noveldownload.websocket.websocketcore.session;

import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 默认的 {@link WebSocketSessionManager} 实现类
 */
public class WebSocketSessionManagerImpl implements WebSocketSessionManager {

    private static Integer userType1 = 1;
    private static Long userId1 = 1L;
    /**
     * id 与 WebSocketSession 映射
     *
     * key：Session 编号
     */
    private final ConcurrentMap<String, WebSocketSession> idSessions = new ConcurrentHashMap<>();

    /**
     * user 与 WebSocketSession 映射
     *
     * key1：用户类型
     * key2：用户编号
     */
    private final ConcurrentMap<Integer, ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>>> userSessions
            = new ConcurrentHashMap<>();

    @Override
    public void addSession(WebSocketSession session) {
        // 添加到 idSessions 中
        idSessions.put(session.getId(), session);
        // 添加到 userSessions 中

        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType1);
        if (userSessionsMap == null) {
            userSessionsMap = new ConcurrentHashMap<>();
            if (userSessions.putIfAbsent(userType1, userSessionsMap) != null) {
                userSessionsMap = userSessions.get(userType1);
            }
        }
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(userId1);
        if (sessions == null) {
            sessions = new CopyOnWriteArrayList<>();
            if (userSessionsMap.putIfAbsent(Long.valueOf(userId1), sessions) != null) {
                sessions = userSessionsMap.get(userId1);
            }
        }
        sessions.add(session);
    }

    @Override
    public void removeSession(WebSocketSession session) {
        // 移除从 idSessions 中
        idSessions.remove(session.getId());
        // 移除从 idSessions 中

        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType1);
        if (userSessionsMap == null) {
            return;
        }
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(userId1);
        sessions.removeIf(session0 -> session0.getId().equals(session.getId()));
        if (CollectionUtils.isEmpty(sessions)) {
            userSessionsMap.remove(userId1, sessions);
        }
    }

    @Override
    public WebSocketSession getSession(String id) {
        return idSessions.get(id);
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);
        if (CollectionUtils.isEmpty(userSessionsMap)) {
            return new ArrayList<>();
        }
        LinkedList<WebSocketSession> result = new LinkedList<>(); // 避免扩容
        for (List<WebSocketSession> sessions : userSessionsMap.values()) {
            if (CollectionUtils.isEmpty(sessions)) {
                continue;
            }
            result.addAll(sessions);
        }
        return result;
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType, Long userId) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);
        if (CollectionUtils.isEmpty(userSessionsMap)) {
            return new ArrayList<>();
        }
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(userId);
        return !CollectionUtils.isEmpty(sessions) ? new ArrayList<>(sessions) : new ArrayList<>();
    }

}
