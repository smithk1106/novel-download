package com.ideaflow.noveldownload.novel.parse;

import cn.hutool.core.util.StrUtil;
import com.ideaflow.noveldownload.novel.context.HttpClientContext;
import com.ideaflow.noveldownload.novel.convert.ChineseConverter;
import com.ideaflow.noveldownload.novel.core.CoverUpdater;
import com.ideaflow.noveldownload.novel.core.Source;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.novel.model.Book;
import com.ideaflow.noveldownload.novel.model.ContentType;
import com.ideaflow.noveldownload.novel.model.Rule;
import com.ideaflow.noveldownload.novel.util.CrawlUtils;
import com.ideaflow.noveldownload.novel.util.JsoupUtils;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class BookParser extends Source {

    public final OkHttpClient client = HttpClientContext.get();

    public BookParser(AppConfig config) {
        super(config);
    }

    @SneakyThrows
    public Book parse(String url) {
        Rule.Book r = this.rule.getBook();

        Document document;
        try (Response resp = CrawlUtils.request(client, url, r.getTimeout())) {
            document = Jsoup.parse(resp.body().string(), r.getBaseUri());
        }

        String bookName = JsoupUtils.selectAndInvokeJs(document, r.getBookName(), getContentType(r.getBookName()));
        String author = JsoupUtils.selectAndInvokeJs(document, r.getAuthor(), getContentType(r.getAuthor()));
        String intro = StrUtil.cleanBlank(JsoupUtils.selectAndInvokeJs(document, r.getIntro(), getContentType(r.getIntro())));
        String coverUrl = JsoupUtils.selectAndInvokeJs(document, r.getCoverUrl(),
                StrUtil.startWith(r.getCoverUrl(), "meta[") ? ContentType.ATTR_CONTENT : ContentType.ATTR_SRC);
        // 以下为非必须属性
        String category = JsoupUtils.selectAndInvokeJs(document, r.getCategory(), getContentType(r.getCategory()));
        String latestChapter = JsoupUtils.selectAndInvokeJs(document, r.getLatestChapter(), getContentType(r.getLatestChapter()));
        String lastUpdateTime = JsoupUtils.selectAndInvokeJs(document, r.getLastUpdateTime(), getContentType(r.getLastUpdateTime()));
        String status = JsoupUtils.selectAndInvokeJs(document, r.getStatus(), getContentType(r.getStatus()));
        String wordCount = JsoupUtils.selectAndInvokeJs(document, r.getWordCount(), getContentType(r.getWordCount()));

        Book book = new Book();
        book.setUrl(url);
        book.setBookName(bookName);
        book.setAuthor(author);
        book.setIntro(intro);
        book.setCoverUrl(CoverUpdater.fetchCover(book, coverUrl));
        book.setCategory(category);
        book.setCategoryId(guessCategory(category).getCode());
        book.setLatestChapter(latestChapter);
        book.setLastUpdateTime(lastUpdateTime);
        book.setStatus(status);
        try {
            book.setWordCount(Long.parseLong(wordCount));
        } catch (NumberFormatException e) {
            book.setWordCount(0L);
        }

        return ChineseConverter.convert(book, this.rule.getLanguage(), config.getLanguage());
    }

    /**
     * 猜测分类
     * 
     * @param categoryString 分类字符串
     * @return 猜测的分类
     */
    public BookCategory guessCategory(String categoryString) {
        BookCategory bookCategory = BookCategory.UNKNOWN;
        
        if (categoryString == null || categoryString.isEmpty()) {
            return bookCategory;
        }
        // 遍历所有枚举值，匹配包含的分类
        for (BookCategory cat : BookCategory.values()) {
            String[] categories = cat.getDescription().split(",");
            for (String catDesc : categories) {
                if (categoryString.contains(catDesc)) {
                    bookCategory = cat;
                    break;
                }
            }
            if (bookCategory != BookCategory.UNKNOWN) {
                break;
            }
        }

        return bookCategory;
    }
    

    private ContentType getContentType(String query) {
        if (StrUtil.isEmpty(query)) {
            return null;
        }
        return query.startsWith("meta[") ? ContentType.ATTR_CONTENT : ContentType.TEXT;
    }

}