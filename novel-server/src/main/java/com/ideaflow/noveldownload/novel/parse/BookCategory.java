package com.ideaflow.noveldownload.novel.parse;
/**
 * 小说分类枚举
 */
public enum BookCategory {
    UNKNOWN(0, "未知"),
    FANTASY(1, "玄幻,奇幻"),
    XIANXIA(2, "仙侠"),
    WUXIA(3, "武侠"),
    MYSTERY(4, "悬疑,侦探"),
    SCIENCE_FICTION(5, "科幻"),
    ROMANCE(6, "言情,古言,现言,幻言"),
    HISTORY(7, "历史"),
    MILITARY(8, "军事"),
    MODERN(9, "都市,现代,体育"),
    COMEDY(10, "女生"),
    GAME(11, "游戏"),
    YOUNGER(12, "青春,校园"),
    TIMETRAVEL(13, "穿越,重生");

    private final int code;
    private final String description;

    int getCode() {
        return code;
    }

    String getDescription() {
        return description;
    }

    BookCategory(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
