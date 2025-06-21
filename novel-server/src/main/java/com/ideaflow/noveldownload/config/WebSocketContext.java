package com.ideaflow.noveldownload.config;

import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;

public class WebSocketContext {
    private static final ThreadLocal<WebSocketMessageSender> threadLocalSender = new ThreadLocal<>();
    // 创建一个 ThreadLocal，用来存储每个线程独立的 String 值
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // 设置当前线程的 String 变量
    public static void set(String value) {
        threadLocal.set(value);
    }

    // 获取当前线程的 String 变量
    public static String getSessionId() {
        return threadLocal.get();
    }

    // 清除当前线程的 String 变量
    public static void clearSessionId() {
        threadLocal.remove();
    }

    // 设置 WebSocketMessageSender 到当前线程
    public static void setSender(WebSocketMessageSender webSocketMessageSender) {
        threadLocalSender.set(webSocketMessageSender);
    }

    // 获取当前线程的 WebSocketMessageSender
    public static WebSocketMessageSender getSender() {
        return threadLocalSender.get();
    }

    // 清除当前线程的 WebSocketMessageSender
    public static void clearSerder() {
        threadLocalSender.remove();
    }
}