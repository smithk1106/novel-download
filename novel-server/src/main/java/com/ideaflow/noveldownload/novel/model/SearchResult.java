package com.ideaflow.noveldownload.novel.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SearchResult {

    private Integer sourceId;
    private String url;
    private String bookName;
    private String author;
    private String intro;
    private String category;
    private String latestChapter;
    private String lastUpdateTime;
    private String status;
    private String wordCount;

}
