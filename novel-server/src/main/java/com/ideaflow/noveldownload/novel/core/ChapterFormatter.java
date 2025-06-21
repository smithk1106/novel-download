package com.ideaflow.noveldownload.novel.core;

import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.novel.model.Rule;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class ChapterFormatter {

    private final AppConfig config;

    /**
     * 格式化正文排版
     */
    public String format(String content) {
        Rule.Chapter rule = new Source(config).rule.getChapter();

        // 标签闭合
        if (rule.isParagraphTagClosed()) {
            // <p>段落</p>
            if ("p".equals(rule.getParagraphTag())) {
                return content;
            } else { // 非 <p> 闭合标签，替换为 <p>
                return content.replaceAll("<(?!p\\b)([^>]+)>(.*?)</\\1>", "<p>$2</p>");
            }
        }
        // 标签不闭合，用某个标签分隔，例如：段落1<br><br>段落2
        String tag = rule.getParagraphTag();
        StringBuilder sb = new StringBuilder();

        for (String s : content.split(tag)) {
            if (!s.isBlank()) {
                sb.append("<p>").append(s).append("</p>");
            }
        }

        return sb.toString();
    }

}