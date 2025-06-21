package com.ideaflow.noveldownload.novel.model;

import lombok.Data;


@Data
public class Book {

    private String url;
    private String bookName;
    private String author;
    private String intro;
    private String category;
    private String coverUrl;
    private String latestChapter;
    private String lastUpdateTime;
    private String status;
    private String wordCount;

}