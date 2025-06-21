package com.ideaflow.noveldownload.websocket.config;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class WebSocketThreadLocal {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> threadLocalCounter = new ThreadLocal<>();

    public static void setThreadLocalValue(String value) {
        threadLocal.set(value);
    }

    public static String getThreadLocalValue() {
        return threadLocal.get();
    }

    public static void removeThreadLocalValue() {
        threadLocal.remove();
    }

    public static void setThreadLocalCounterValue(int value) {
        threadLocalCounter.set(new AtomicInteger(value));
    }

    public static int getThreadLocalCounterValue() {
        return threadLocalCounter.get().get();
    }

    public static void incrementThreadLocalCounterValue() {
        threadLocalCounter.get().incrementAndGet();
    }

    public static void removeThreadLocalCounterValue() {
        threadLocalCounter.remove();
    }
}