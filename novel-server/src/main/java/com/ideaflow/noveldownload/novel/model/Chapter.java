package com.ideaflow.noveldownload.novel.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Chapter {

    private String url;
    private String title;
    private String content;
    private Integer order;

}