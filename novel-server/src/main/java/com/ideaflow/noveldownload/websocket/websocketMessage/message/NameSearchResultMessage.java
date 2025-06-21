package com.ideaflow.noveldownload.websocket.websocketMessage.message;

import lombok.Data;


@Data
public class NameSearchResultMessage {


    /**
     * 书源id
     */
    private String sourceId;
    /**
     * bookName
     */
    private String bookName;
    /**
     * author
     */
    private String author;

}
