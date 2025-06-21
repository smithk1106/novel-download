package com.ideaflow.noveldownload.novel.handle;


import com.ideaflow.noveldownload.novel.model.Book;

import java.io.File;


public interface PostProcessingHandler {

    void handle(Book book, File saveDir);

}

