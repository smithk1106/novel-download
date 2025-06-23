package com.ideaflow.noveldownload.novel.handle;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ideaflow.noveldownload.config.WebSocketContext;
import com.ideaflow.noveldownload.novel.context.BookContext;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.novel.model.Book;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Set;


@AllArgsConstructor
public class CrawlerPostHandler {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("txt", "epub", "pdf");
    private final AppConfig config;

    @SneakyThrows
    public void handle(File saveDir) {
        Book book = BookContext.get();
        String extName = config.getExtName();
        StringBuilder s = new StringBuilder(StrUtil.format("\n<== 《{}》（{}）下载完毕，", book.getBookName(), book.getAuthor()));

        if (ALLOWED_EXTENSIONS.contains(extName.toLowerCase())) {
            s.append("正在合并为 ").append(extName.toUpperCase());
        }
        if ("html".equals(extName)) {
            s.append("正在生成 HTML 目录文件");
        }

        WebSocketMessageSender webSocketMessageSender = WebSocketContext.getSender();
        String sessionId = WebSocketContext.getSessionId();
        webSocketMessageSender.send(sessionId, "NovelDownloadConsoleMessageListener", JSONUtil.toJsonStr(s.append(" ...")));


        // 等待文件系统更新索引
        int attempts = 10;
        while (FileUtil.isDirEmpty(saveDir) && attempts > 0) {
            Thread.sleep(100);
            attempts--;
        }

        PostHandlerFactory.getHandler(extName, config).handle(book, saveDir);

        if (ALLOWED_EXTENSIONS.contains(extName.toLowerCase()) && config.getPreserveChapterCache() == 0) {
            FileUtil.del(saveDir);
        }
    }
}