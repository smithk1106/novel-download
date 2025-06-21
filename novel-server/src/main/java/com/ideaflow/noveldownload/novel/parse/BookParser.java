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
        book.setLatestChapter(latestChapter);
        book.setLastUpdateTime(lastUpdateTime);
        book.setStatus(status);
        book.setWordCount(wordCount);

        return ChineseConverter.convert(book, this.rule.getLanguage(), config.getLanguage());
    }

    private ContentType getContentType(String query) {
        if (StrUtil.isEmpty(query)) {
            return null;
        }
        return query.startsWith("meta[") ? ContentType.ATTR_CONTENT : ContentType.TEXT;
    }

}