package com.ideaflow.noveldownload.websocket.websocketMessage.message;

import lombok.Data;


@Data
public class DownloadSendMessage {
    /**
     * 书源id
     */
    private Integer sourceId;
    /**
     * 検索结果id
     */    
    private String searchResultId;
    /**
     * 下载类型：默认下载全部，1-下载指定范围章节，2-下载最新章节
     */
    private Integer downloadType;
    /**
     * 开始章节
     */
    private Integer startChapter;
    /**
     * 结束章节
     */
    private Integer endChapter;
    /**
     * 最新章节数
     */
    private Integer latestChapterCount;
}
