package com.ideaflow.noveldownload.novel.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class ExceptionUtils {

    // 随机抛异常，测试用
    public void randomThrow() {
        if (System.currentTimeMillis() % 2 == 0) {
            throw new NullPointerException("随机抛 NPE");
        }
    }

}