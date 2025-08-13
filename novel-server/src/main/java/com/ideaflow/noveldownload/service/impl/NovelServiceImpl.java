package com.ideaflow.noveldownload.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ideaflow.noveldownload.entity.ChapterEntity;
import com.ideaflow.noveldownload.entity.NovelEntity;
import com.ideaflow.noveldownload.mapper.ChapterMapper;
import com.ideaflow.noveldownload.mapper.NovelMapper;
import com.ideaflow.noveldownload.novel.model.Book;
import com.ideaflow.noveldownload.novel.model.Chapter;
import com.ideaflow.noveldownload.service.NovelService;

import jakarta.annotation.Resource;

@Service
public class NovelServiceImpl implements NovelService {

    @Resource
    private NovelMapper novelMapper;

    @Resource
    private ChapterMapper chapterMapper;

    @Override
    public Long saveBook(Book book) {
        NovelEntity novelEntity = getEntityFromBook(book);
        List<NovelEntity> list = novelMapper.selectByMap(Map.of("name", book.getBookName()));
        if (list.isEmpty()) {
            if (novelMapper.insert(novelEntity) > 0){
                book.setId(novelEntity.getId());
            } else {
                book.setId(0L);
            }
        } else {
            book.setId(list.get(0).getId());
            novelEntity.setId(book.getId());
            novelMapper.updateById(novelEntity);
        }

        return book.getId();
    }

    @Override
    public Book getBookById(Long id) {
        NovelEntity novelEntity = novelMapper.selectById(id);
        if (novelEntity == null) {
            return null;
        }
        return getBookFromEntity(novelEntity);
    }

    @Override
    public Book getBookByName(String bookName) {
        List<NovelEntity> list = novelMapper.selectByMap(Map.of("name", bookName));
        if (list.isEmpty()) {
            return null;
        }
        return getBookFromEntity(list.get(0));
    }

    @Override
    public Long updateBook(Book book) {
        Long bookId = 0L;
        NovelEntity novelEntity = getEntityFromBook(book);
        if (novelMapper.updateById(novelEntity) > 0) {
            bookId = novelEntity.getId();
        }
        return bookId;
    }

    @Override
    public Long saveChapter(Chapter chapter) {
        ChapterEntity chapterEntity = getEntityFromChapter(chapter);

        Map<String, Object> keyMap = Map.of(
            "book_id", chapter.getBookId(),
            "title", chapter.getTitle()
        );
        List<ChapterEntity> list = chapterMapper.selectByMap(keyMap);
        if (list.isEmpty()) {
            if (chapterMapper.insert(chapterEntity) > 0){
                chapter.setId(chapterEntity.getId());
            } else {
                chapter.setId(0L);
            }
        } else {
            chapter.setId(list.get(0).getId());
            chapterEntity.setId(chapter.getId());
            chapterMapper.updateById(chapterEntity);
        }

        return chapter.getId();
    }

    @Override
    public Chapter getChapterById(Long id) {
        ChapterEntity chapterEntity = chapterMapper.selectById(id);
        if (chapterEntity == null) {
            return null;
        }

        return getChapterFromEntity(chapterEntity);
    }

    @Override
    public Chapter getChapterByBookIdAndTitle(Long bookId, String title) {
        Map<String, Object> keyMap = Map.of(
            "book_id", bookId,
            "title", title
        );
        List<ChapterEntity> list = chapterMapper.selectByMap(keyMap);
        if (list.isEmpty()) {
            return null;
        }
        ChapterEntity chapterEntity = list.get(0);

        return getChapterFromEntity(chapterEntity);
    }

    @Override
    public Long updateChapter(Chapter chapter) {
        Long id = 0L;
        ChapterEntity chapterEntity = getEntityFromChapter(chapter);
        if (chapterMapper.updateById(chapterEntity) > 0) {
            id = chapterEntity.getId();
        }
        return id;
    }
    

    private Book getBookFromEntity(NovelEntity novelEntity) {
        Book book = new Book();

        book.setId(novelEntity.getId());
        book.setBookName(novelEntity.getName());
        book.setCoverUrl(novelEntity.getCover());
        book.setAuthor(novelEntity.getAuthor());
        book.setIntro(novelEntity.getIntro());
        book.setCategory(String.valueOf(novelEntity.getCategoryId()));
        book.setLatestChapter(novelEntity.getLatestChapter());
        book.setLastUpdateTime(novelEntity.getLastUpdateTime() == null ? "" : novelEntity.getLastUpdateTime().toString());
        book.setStatus(novelEntity.getStatus() == 0 ? "完结" : "连载");
        book.setWordCount(novelEntity.getWordCount());
        book.setSaveType(novelEntity.getSaveType());
        book.setDownloadUrl(novelEntity.getDownloadUrl());

        return book;
    }

    private NovelEntity getEntityFromBook(Book book) {
        NovelEntity novelEntity = new NovelEntity();

        novelEntity = new NovelEntity();
        novelEntity.setName(book.getBookName());
        novelEntity.setCover(book.getCoverUrl());
        novelEntity.setAuthor(book.getAuthor());
        novelEntity.setIntro(book.getIntro());
        novelEntity.setCategoryId(book.getCategoryId());
        novelEntity.setLatestChapter(book.getLatestChapter());
        if (book.getLastUpdateTime() == null || book.getLastUpdateTime().isEmpty()) {
            novelEntity.setLastUpdateTime(Date.valueOf(LocalDate.now()));
        } else {
            // 处理可能的日期格式问题
            try {
                novelEntity.setLastUpdateTime(Date.valueOf(book.getLastUpdateTime()));
            } catch (IllegalArgumentException e) {
                // 如果转换失败，使用当前日期
                novelEntity.setLastUpdateTime(Date.valueOf(LocalDate.now()));
            }
        }
        novelEntity.setStatus("完结".equalsIgnoreCase(book.getStatus()) ? 0 : 1);
        novelEntity.setWordCount(book.getWordCount());
        novelEntity.setSaveType(book.getSaveType());
        novelEntity.setDownloadUrl(book.getDownloadUrl());
        novelEntity.setId(book.getId());

        return novelEntity;
    }

    private ChapterEntity getEntityFromChapter(Chapter chapter) {
        ChapterEntity chapterEntity = new ChapterEntity();
    
        chapterEntity.setId(chapter.getId());
        chapterEntity.setBookId(chapter.getBookId());
        chapterEntity.setTitle(chapter.getTitle());
        chapterEntity.setOrder1(chapter.getOrder());
        chapterEntity.setContent(chapter.getCleanContent());
        chapterEntity.setWordCount((long)chapter.getContent().length());

        return chapterEntity;
    }

    private Chapter getChapterFromEntity(ChapterEntity chapterEntity) {
        return Chapter.builder()
                .id(chapterEntity.getId())
                .bookId(chapterEntity.getBookId())
                .title(chapterEntity.getTitle())
                .order(chapterEntity.getOrder1())
                .wordCount(chapterEntity.getWordCount())
                .content(chapterEntity.getContent())
                .build();
    }
}
