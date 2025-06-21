package com.ideaflow.noveldownload.novel.context;


import com.ideaflow.noveldownload.novel.model.Book;
import com.ideaflow.noveldownload.novel.util.FileUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BookContext {

    // 子线程会自动继承父线程的值
    private static final InheritableThreadLocal<Book> currentBook = new InheritableThreadLocal<>();

    public void set(Book book) {
        book.setBookName(FileUtils.sanitizeFileName(book.getBookName()));
        book.setAuthor(FileUtils.sanitizeFileName(book.getAuthor()));
        currentBook.set(book);
    }

    public Book get() {
        return currentBook.get();
    }

    public void clear() {
        currentBook.remove();
    }

}