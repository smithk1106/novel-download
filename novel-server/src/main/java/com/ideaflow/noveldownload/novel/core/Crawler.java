package com.ideaflow.noveldownload.novel.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.fusesource.jansi.AnsiRenderer.render;

import com.ideaflow.noveldownload.config.WebSocketContext;
import static com.ideaflow.noveldownload.constans.CommonConst.NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER;
import static com.ideaflow.noveldownload.constans.CommonConst.NOVEL_NAME_SEARCH_CONSOLE_MESSAGE_LISTENER;
import com.ideaflow.noveldownload.novel.context.BookContext;
import com.ideaflow.noveldownload.novel.handle.CrawlerPostHandler;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.novel.model.Book;
import com.ideaflow.noveldownload.novel.model.Chapter;
import com.ideaflow.noveldownload.novel.model.SearchResult;
import com.ideaflow.noveldownload.novel.parse.BookParser;
import com.ideaflow.noveldownload.novel.parse.ChapterParser;
import com.ideaflow.noveldownload.novel.parse.SearchParser;
import com.ideaflow.noveldownload.novel.util.FileUtils;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Crawler {

    private final AppConfig config;
    private String bookDir;

    private int digitCount;



    public Crawler(AppConfig config) {
        this.config = config;
    }

    /**
     * 搜索小说
     *
     * @param keyword 关键字
     * @return 匹配的小说列表
     */
    public List<SearchResult> search(String keyword) {
        WebSocketMessageSender webSocketMessageSender = WebSocketContext.getSender();
        String sessionId = WebSocketContext.getSessionId();

        webSocketMessageSender.send(sessionId, NOVEL_NAME_SEARCH_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("[i]正在搜索 %s...",keyword)));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        SearchParser searchResultParser = new SearchParser(config);
        List<SearchResult> searchResults = searchResultParser.parse(keyword);

        stopWatch.stop();
        webSocketMessageSender.send(sessionId, NOVEL_NAME_SEARCH_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("[i]搜索到 %s 条记录，耗时 %s s", searchResults.size(), NumberUtil.round(stopWatch.getTotalTimeSeconds(), 2))));

        return searchResults;
    }


    /**
     * 爬取小说
     *
     * @param bookUrl 详情页链接
     * @param toc     章节目录
     */
    @SneakyThrows
    public Book crawl(String bookUrl, List<Chapter> toc) {
        digitCount = String.valueOf(toc.size()).length();
        Book book = new BookParser(config).parse(bookUrl);
        BookContext.set(book);
        WebSocketMessageSender webSocketMessageSender = WebSocketContext.getSender();
        String sessionId = WebSocketContext.getSessionId();

        // 下载临时目录名格式：书名(作者) EXT
        bookDir = FileUtils.sanitizeFileName("%s(%s) %s".formatted(book.getBookName(), book.getAuthor(), config.getExtName().toUpperCase()));
        // 必须 new File()，否则无法使用 . 和 ..
        File dir = FileUtil.mkdir(new File(config.getDownloadPath() + File.separator + bookDir));
        if (!dir.exists()) {
            webSocketMessageSender.send(sessionId, NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("[i]创建下载目录失败:%s,请检查是否有创建文件的权限",dir.getPath())));
            return null;
        }

        // HTML模板相关文件
        if (config.getExtName().equalsIgnoreCase("html")) {
            // Copy css file to download folder
            exportResourceFile("/templates/css/style.css", new File(dir.getAbsolutePath() + File.separator + "css" + File.separator + "style.css"));
            // Copy js file to download folder
            exportResourceFile("/templates/js/chapter.js", new File(dir.getAbsolutePath() + File.separator + "js" + File.separator + "chapter.js"));
        }

        // 下载封面
        downloadCover(book, dir);

        int autoThreads = config.getThreads() == -1 ? RuntimeUtil.getProcessorCount() * 2 : config.getThreads();
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(autoThreads);
        // 阻塞主线程，用于计时
        CountDownLatch latch = new CountDownLatch(toc.size());

        webSocketMessageSender.send(
            sessionId,
            NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER,
            JSONUtil.toJsonStr(String.format("[i]开始下载《%s》（%s） 共计 %s 章 | 线程数：%s",book.getBookName(), book.getAuthor(), toc.size(), autoThreads))
        );

        if (config.getShowDownloadLog() == 0) {
            webSocketMessageSender.send(sessionId,NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, "[i]下载日志已关闭，请耐心等待...");
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ChapterParser chapterParser = new ChapterParser(config);

        // 爬取&下载章节
        toc.forEach(item -> executor.execute(() -> {
            try {
                WebSocketContext.setSender(webSocketMessageSender);
                WebSocketContext.set(sessionId);
                createChapterFile(chapterParser.parse(item, latch));
                if (config.getShowDownloadLog() == 1) {
                    webSocketMessageSender.send(
                        sessionId,
                        NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER,
                        JSONUtil.toJsonStr(String.format("[i][%d/%d]已下载: %s, %s", toc.size() - latch.getCount(), toc.size(), item.getTitle(), item.getUrl()))
                    );
                }
            } finally {
                WebSocketContext.clearSessionId();
                WebSocketContext.clearSerder();
            }

        }));

        // 阻塞主线程，等待全部章节下载完毕
        latch.await();
        new CrawlerPostHandler(config).handle(dir);
        stopWatch.stop();

        executor.shutdown();
        BookContext.clear();

        return book;
    }

    /**
     * 保存章节
     */
    private void createChapterFile(Chapter chapter) {
        if (chapter == null) return;

        try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(generateChapterPath(chapter)))) {
            fos.write(chapter.getContent().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Console.error(e);
        }
    }

    private String generateChapterPath(Chapter chapter) {
        String parentPath = config.getDownloadPath() + File.separator + bookDir + File.separator;
        // 文件名下划线前的数字前补零
        String order = digitCount >= String.valueOf(chapter.getOrder()).length()
                ? StrUtil.padPre(chapter.getOrder() + "", digitCount, '0') // 全本下载
                : String.valueOf(chapter.getOrder()); // 非全本下载

        return parentPath + order + switch (config.getExtName()) {
            // 下划线用于兼容，不要删除，见 com/pcdd/sonovel/handle/HtmlTocHandler.java:28
            case "html" -> "_.html";
            case "txt" -> "_" + FileUtils.sanitizeFileName(chapter.getTitle()) + ".txt";
            // 转换前的格式为 html
            case "epub", "pdf" -> "_" + FileUtils.sanitizeFileName(chapter.getTitle()) + ".html";
            default -> throw new IllegalStateException("暂不支持的下载格式: " + config.getExtName());
        };
    }

    private boolean exportResourceFile(String resourcePath, File targetPath) {
        try (InputStream input = getClass().getResourceAsStream(resourcePath)) {
            if (input == null) {
                Console.error("[E]资源文件不存在: {}", resourcePath);
                return false;
            }
            if (!targetPath.getParentFile().exists()) {
                targetPath.getParentFile().mkdirs();
            }
            FileOutputStream output = new FileOutputStream(targetPath);
            output.write(input.readAllBytes());
            output.close();
            Console.error("[D]已导出资源文件: {}", targetPath);
            return true;
        } catch (IOException e) {
            Console.error("[E]导出资源文件失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 下载封面失败会导致生成中断，必须捕获异常
     */
    private void downloadCover(Book book, File saveDir) {
        try {
            Console.log("[i]正在下载封面：{}", book.getCoverUrl());
            File coverFile = HttpUtil.downloadFileFromUrl(book.getCoverUrl(), FileUtils.resolvePath(saveDir.toString()));
            FileUtil.rename(coverFile, "0_封面." + FileUtil.getType(coverFile), true);
        } catch (Exception e) {
            Console.error(render("[E]封面下载失败：{}", "red"), e.getMessage());
        }
    }
}