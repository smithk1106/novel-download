package com.ideaflow.noveldownload.websocket.websocketMessage.message;

import lombok.Data;


@Data
public class NameSearchSendMessage {


    /**
     * 书源id
     */
    private String sourceId;
    /**
     * bookName or author
     *
     */
    private String bookName;
    /**
     * author
     */
    private String author;

    /**
     *   <option value="fuzzy">模糊</option>
     *   <option value="exact">精确</option>
     */
    private String searchType;

}
