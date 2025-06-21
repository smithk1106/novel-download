package com.ideaflow.noveldownload.novel.handle;


import com.ideaflow.noveldownload.novel.model.Book;

import java.io.File;

/**
 * @author pcdd
 * Created at 2024/12/4
 */
public interface PostProcessingHandler {

    void handle(Book book, File saveDir);

}

