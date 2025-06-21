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


}
