package com.ideaflow.noveldownload.novel.handle;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ideaflow.noveldownload.config.WebSocketContext;
import com.ideaflow.noveldownload.novel.util.FileUtils;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;
import io.documentnode.epub4j.domain.*;
import io.documentnode.epub4j.epub.EpubWriter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import static com.ideaflow.noveldownload.constans.CommonConst.NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER;
import static org.fusesource.jansi.AnsiRenderer.render;

/**
 * @author pcdd
 * Created at 2024/12/4
 */
public class EpubMergeHandler implements PostProcessingHandler {

    public static final String COVER_NAME = "cover.html";

    @SneakyThrows
    @Override
    public void handle(com.ideaflow.noveldownload.novel.model.Book b, File saveDir) {
        WebSocketMessageSender webSocketMessageSender = WebSocketContext.getSender();
        String sessionId = WebSocketContext.getSessionId();

        if (FileUtil.isDirEmpty(saveDir)) {
            Console.error(render("<== 《{}》（{}）下载章节数为 0，取消生成 EPUB", "red"), b.getBookName(), b.getAuthor());
            webSocketMessageSender.send(sessionId, NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("<== 《%s》（%s）下载章节数为 0，取消生成 EPUB",b.getBookName(), b.getAuthor())));
            return;
        }



        Book book = new Book();
        // content.opf > metadata
        Metadata meta = book.getMetadata();
        meta.addTitle(b.getBookName());
        meta.setAuthors(List.of(new Author(b.getAuthor())));
        meta.addDescription(b.getIntro());
        // 下载封面失败会导致生成 epub 中断
        try {
            webSocketMessageSender.send(sessionId, NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("<== 正在下载封面： %s...",b.getCoverUrl())));
            byte[] bytes = HttpUtil.downloadBytes(b.getCoverUrl());
            book.setCoverImage(new Resource(bytes, "cover.jpg"));
            // 添加封面页
            book.addSection("封面", new Resource(ResourceUtil.readBytes("templates/chapter_cover.html"), COVER_NAME));
        } catch (Exception e) {
            webSocketMessageSender.send(sessionId, NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("<== 封面下载失败： %s...",e.getMessage())));
        }
        // 不设置会导致 Apple Books 无法使用苹方字体
        meta.setLanguage("zh");
        meta.setDates(List.of(new io.documentnode.epub4j.domain.Date(new Date())));
        meta.addPublisher("ideaflow-novel-download");
        meta.setRights(List.of("本电子书由 Ideaflow-novel-download(https://github.com/Idea-flow/novel-download) 制作生成。仅供交流使用，不得用于商业用途。"));

        // content.opf > manifest
        List<File> files = FileUtils.sortFilesByName(saveDir);
        int len = String.valueOf(files.size()).length();
        // 添加正文页
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            // 截取第一个 _ 后的字符串，即章节名
            String title = StrUtil.subAfter(FileUtil.mainName(file), "_", false);
            String id = StrUtil.padPre(String.valueOf(i + 1), len, '0');
            book.addSection(title, new Resource(id, FileUtil.readBytes(file), id + ".html", MediaTypes.XHTML));
        }

        // 设置 guide，用于指定封面
        book.getGuide().addReference(new GuideReference(new Resource(ResourceUtil.readBytes("templates/chapter_cover.html"), COVER_NAME), "封面", COVER_NAME));
        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(book, new FileOutputStream(StrUtil.format("{}/{}({}).epub", saveDir.getParent(), b.getBookName(), b.getAuthor())));
    }

}