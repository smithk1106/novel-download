package com.ideaflow.noveldownload.novel.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Chapter {

    private Long id; // 主键id
    @Builder.Default
    private Long bookId = 0L; // 小说id
    private String url;
    private String title;
    private String content;
    private String cleanContent;
    @Builder.Default
    private Long wordCount = 0L; // 字数
    @Builder.Default
    private Long order = 0L;

}