package com.ideaflow.noveldownload.websocket.websocketMessage.message;

import lombok.Data;


@Data
public class DownloadSendMessage {


    /**
     * 书源id
     */
    private Integer sourceId;
    /**
     * searchResultId
     */
    private String searchResultId;



    private Integer downloadType; // 下载类型：默认下载全部，1-下载指定范围章节，2-下载最新章节


    private Integer startChapter; // 开始章节
    private Integer endChapter; // 开始章节

    private Integer latestChapterCount; // 最新多少章节

}
