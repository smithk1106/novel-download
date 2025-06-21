package com.ideaflow.noveldownload.websocket.websocketMessage;


import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.ideaflow.noveldownload.config.WebSocketContext;
import com.ideaflow.noveldownload.entity.AppConfigEntity;
import com.ideaflow.noveldownload.entity.NovelEntity;
import com.ideaflow.noveldownload.entity.SearchResultEntity;
import com.ideaflow.noveldownload.mapper.AppConfigMapper;
import com.ideaflow.noveldownload.mapper.NovelMapper;
import com.ideaflow.noveldownload.mapper.SearchResultMapper;
import com.ideaflow.noveldownload.novel.context.HttpClientContext;
import com.ideaflow.noveldownload.novel.core.Crawler;
import com.ideaflow.noveldownload.novel.core.OkHttpClientFactory;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.novel.model.Book;
import com.ideaflow.noveldownload.novel.model.Chapter;
import com.ideaflow.noveldownload.novel.model.SearchResult;
import com.ideaflow.noveldownload.novel.parse.TocParser;
import com.ideaflow.noveldownload.websocket.config.WebSocketThreadLocal;
import com.ideaflow.noveldownload.websocket.websocketMessage.message.DownloadSendMessage;
import com.ideaflow.noveldownload.websocket.websocketcore.listener.WebSocketMessageListener;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.ideaflow.noveldownload.constans.CommonConst.NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER;


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
    private NovelMapper novelMapper;

    @Override
    public void onMessage(WebSocketSession session, DownloadSendMessage message) {
        try {
            AppConfigEntity appConfigEntity = appConfigMapper.selectById(1);
            AppConfig config = JSONUtil.toBean(appConfigEntity.getConfigValue(), AppConfig.class);
            SearchResultEntity searchResultEntity = searchResultMapper.selectById(message.getSearchResultId());
            WebSocketThreadLocal.setThreadLocalValue(session.getId());
            if (Objects.isNull(searchResultEntity)) {
                webSocketMessageSender.send(session.getId(), "NovelDownloadConsoleMessageListener", JSONUtil.toJsonStr("数据为空,请重新尝试搜索:id:"+message.getSearchResultId()));
                return;
            }
            if (Objects.isNull(config.getSourceId()))   {
                config.setSourceId(searchResultEntity.getSourceId());
            }
            HttpClientContext.set(OkHttpClientFactory.create(config, true));
            // 查找数据
            webSocketMessageSender.send(session.getId(), NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr("<== 正在获取章节目录 ..."));

            SearchResult searchResult = JSONUtil.toBean(searchResultEntity.getContent(), SearchResult.class);
            TocParser catalogParser = new TocParser(config);
            List<Chapter> catalogs = catalogParser.parse(searchResult.getUrl(), 1, Integer.MAX_VALUE);
            String r1 =  String.format("<== 你选择了《%s》(%s)，共计 %s 章,开始下载全本,请稍后",searchResult.getBookName(),searchResult.getAuthor(),catalogs.size());
            webSocketMessageSender.send(session.getId(), NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(r1));

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            WebSocketContext.setSender(webSocketMessageSender);
            WebSocketContext.set(session.getId());

            Book book = new Crawler(config).crawl(searchResult.getUrl(), catalogs);
            stopWatch.stop();
            double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
            NovelEntity novelEntity = new NovelEntity();
            novelEntity.setName(searchResult.getBookName());
            novelEntity.setCover(book.getCoverUrl());
            novelEntity.setAuthor(searchResult.getAuthor());
            novelEntity.setDownloadUrl(config.getDownloadPath()+ File.separator+searchResult.getBookName()+ "."+config.getExtName());
            novelMapper.insert(novelEntity);
            webSocketMessageSender.send(session.getId(), NOVEL_DOWNLOAD_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("<== 完成！总耗时 %s s,请取我的书库查看",NumberUtil.round(totalTimeSeconds, 2),novelEntity.getId())));
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
