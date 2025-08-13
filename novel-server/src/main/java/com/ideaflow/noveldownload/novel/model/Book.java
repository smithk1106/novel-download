package com.ideaflow.noveldownload.novel.model;

import lombok.Data;


@Data
public class Book {
    private Long id;
    private String url;
    private String bookName;
    private String author;
    private String intro;
    private String category;
    private int categoryId = 0;
    private String coverUrl;
    private String latestChapter;
    private String lastUpdateTime;
    private String status;
    private Long wordCount = 0L;
    private String saveType;
    private String downloadUrl;
}