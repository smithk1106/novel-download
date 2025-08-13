package com.ideaflow.noveldownload.service;

import com.ideaflow.noveldownload.novel.model.Book;
import com.ideaflow.noveldownload.novel.model.Chapter;

public interface NovelService {
    Long saveBook(Book book);
    Book getBookById(Long id);
    Book getBookByName(String bookName);
    Long updateBook(Book book);

    Long saveChapter(Chapter chapter);
    Chapter getChapterById(Long id);
    Chapter getChapterByBookIdAndTitle(Long bookId, String title);
    Long updateChapter(Chapter chapter);
}
