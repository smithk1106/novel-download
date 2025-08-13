package com.ideaflow.noveldownload.websocket.websocketMessage;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.ideaflow.noveldownload.config.WebSocketContext;
import com.ideaflow.noveldownload.constans.CommonConst;

import static com.ideaflow.noveldownload.constans.CommonConst.NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER;
import com.ideaflow.noveldownload.entity.AppConfigEntity;
import com.ideaflow.noveldownload.entity.SearchResultEntity;
import com.ideaflow.noveldownload.mapper.AppConfigMapper;
import com.ideaflow.noveldownload.mapper.SearchResultMapper;
import com.ideaflow.noveldownload.novel.context.HttpClientContext;
import com.ideaflow.noveldownload.novel.core.Crawler;
import com.ideaflow.noveldownload.novel.core.OkHttpClientFactory;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.novel.model.Book;
import com.ideaflow.noveldownload.novel.model.Chapter;
import com.ideaflow.noveldownload.novel.model.SearchResult;
import com.ideaflow.noveldownload.novel.parse.TocParser;
import com.ideaflow.noveldownload.service.NovelService;
import com.ideaflow.noveldownload.websocket.config.WebSocketThreadLocal;
import com.ideaflow.noveldownload.websocket.websocketMessage.message.DownloadSendMessage;
import com.ideaflow.noveldownload.websocket.websocketcore.listener.WebSocketMessageListener;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;


/**
 * WebSocket 示例：单发消息
 *
 * @author ideaflow
 */
@Component
public class NovelDownloadMessageListener implements WebSocketMessageListener<DownloadSendMessage> {

    @Resource
    private WebSocketMessageSender webSocketMessageSender;

    @Resource
    private AppConfigMapper appConfigMapper;

    @Resource
    private SearchResultMapper searchResultMapper;

    @Resource
    private NovelService novelService;

    @Override
    public void onMessage(WebSocketSession session, DownloadSendMessage message) {
        try {
            AppConfigEntity appConfigEntity = appConfigMapper.selectById(1);
            AppConfig config = JSONUtil.toBean(appConfigEntity.getConfigValue(), AppConfig.class);
            SearchResultEntity searchResultEntity = searchResultMapper.selectById(message.getSearchResultId());
            WebSocketThreadLocal.setThreadLocalValue(session.getId());
            if (Objects.isNull(searchResultEntity)) {
                webSocketMessageSender.send(session.getId(), "NovelDownloadConsoleMessageListener", JSONUtil.toJsonStr("[E]数据为空,请重新尝试搜索:id:"+message.getSearchResultId()));
                return;
            }
            config.setSourceId(searchResultEntity.getSourceId());
            HttpClientContext.set(OkHttpClientFactory.create(config, true));

            // 查找数据
            webSocketMessageSender.send(session.getId(), NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr("[i]正在获取章节目录 ..."));

            SearchResult searchResult = JSONUtil.toBean(searchResultEntity.getContent(), SearchResult.class);
            TocParser catalogParser = new TocParser(config);

            List<Chapter> catalogs = catalogParser.parse(searchResult.getUrl(), 1, Integer.MAX_VALUE);
            List<Chapter> downloadCatalogs = new ArrayList<>();
            if (message.getDownloadType() == null || message.getDownloadType() == 0) {
                // 默认下载全部
                downloadCatalogs.addAll(catalogs);
            } else if (message.getDownloadType() == 1) {
                int start = message.getStartChapter() != null ? message.getStartChapter() : 1;
                int end = message.getEndChapter() != null ? message.getEndChapter() : catalogs.size();
                start = Math.min(start, catalogs.size());
                end = Math.min(catalogs.size(), end);
                if (start < 1  || start > end) {
                    webSocketMessageSender.send(session.getId(), NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr("[E]章节范围参数不合法，请检查后重试。"));
                    return;
                }
                start = Math.max(1, start);
                downloadCatalogs.addAll(catalogs.subList(start - 1, end));
            } else if (message.getDownloadType() == 2) {
                // 下载最新章节
                int count = message.getLatestChapterCount() != null ? message.getLatestChapterCount() : 1;
                count = Math.min(count, catalogs.size());
                if (count < 1 ) {
                    webSocketMessageSender.send(session.getId(), NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr("[E]最新章节数量参数不合法，请检查后重试。"));
                    return;
                }
                if (count > 0) {
                    downloadCatalogs.addAll(catalogs.subList(catalogs.size() - count, catalogs.size()));
                }
            }

            String r1 =  String.format("[i]你选择了《%s》(%s)，共计 %s 章 数据源:%s %s,开始下载全本,请稍后",searchResult.getBookName(),searchResult.getAuthor(),catalogs.size(),config.getSourceId(),searchResult.getUrl());
            webSocketMessageSender.send(session.getId(), NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(r1));

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            WebSocketContext.setSender(webSocketMessageSender);
            WebSocketContext.set(session.getId());

            Book book = new Crawler(config, novelService).crawl(searchResult.getUrl(), downloadCatalogs, String.valueOf(catalogs.size()).length());

            Long bookId = 0L;
            if (CommonConst.SAVE_TYPE_HTML.equalsIgnoreCase(config.getExtName())) {
                bookId = novelService.updateBook(book);
            } else {
                // 保存小说信息
                bookId = novelService.saveBook(book);
            }

            stopWatch.stop();
            double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
            webSocketMessageSender.send(session.getId(), NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("[i]完成！总耗时 %s s,请取我的书库查看",NumberUtil.round(totalTimeSeconds, 2),bookId)));
        } finally {
            WebSocketContext.clearSessionId();
            WebSocketContext.clearSerder();
        }
    }

    @Override
    public String getType() {
        return "NovelDownloadMessageListener";
    }
}
