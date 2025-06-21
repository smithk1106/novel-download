package com.ideaflow.noveldownload.websocket.websocketMessage;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.ideaflow.noveldownload.config.WebSocketContext;
import com.ideaflow.noveldownload.entity.AppConfigEntity;
import com.ideaflow.noveldownload.entity.SearchResultEntity;
import com.ideaflow.noveldownload.mapper.AppConfigMapper;
import com.ideaflow.noveldownload.mapper.SearchResultMapper;
import com.ideaflow.noveldownload.novel.context.HttpClientContext;
import com.ideaflow.noveldownload.novel.core.Crawler;
import com.ideaflow.noveldownload.novel.core.OkHttpClientFactory;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.novel.model.SearchResult;
import com.ideaflow.noveldownload.websocket.websocketMessage.message.NameSearchSendMessage;
import com.ideaflow.noveldownload.websocket.websocketcore.listener.WebSocketMessageListener;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 *
 * @author ideaflow
 */
@Component
public class NovelNameSearchMessageListener implements WebSocketMessageListener<NameSearchSendMessage> {

    @Resource
    private WebSocketMessageSender webSocketMessageSender;

    @Resource
    private AppConfigMapper appConfigMapper;

    @Resource
    private SearchResultMapper searchResultMapper;

    @Override
    public void onMessage(WebSocketSession session, NameSearchSendMessage message) {
        AppConfigEntity appConfigEntity = appConfigMapper.selectById(1);
        boolean isExact = "exact".equals(message.getSearchType()); //是否精确查询
        AppConfig config = JSONUtil.toBean(appConfigEntity.getConfigValue(), AppConfig.class);
        if (Objects.isNull(config.getSourceId()))   {
            config.setSourceId(RandomUtil.randomInt(1, 5));
        }

        HttpClientContext.set(OkHttpClientFactory.create(config, true));

        WebSocketContext.setSender(webSocketMessageSender);
        WebSocketContext.set(session.getId());

        webSocketMessageSender.send(session.getId(), "NovelNameSearchConsoleMessageListener", JSONUtil.toJsonStr(String.format("<== 数据源:%s", config.getSourceId())));
        // 查找数据
        List<SearchResult> results = new Crawler(config).search(message.getBookName());


        List<SearchResultEntity> insertList = results.stream().filter(v->{
            if (isExact){
                if ( v.getBookName().equals(message.getBookName()) || v.getAuthor().equals(message.getBookName()) ) {
                    return true;
                }
                return false;
            }else  {
                return true;
            }
        }).map(v -> {
            SearchResultEntity searchResultEntity = new SearchResultEntity();
            searchResultEntity.setSourceId(config.getSourceId());
            searchResultEntity.setContent(JSONUtil.toJsonStr(v));
            return searchResultEntity;
        }).collect(Collectors.toList());
        if (!insertList.isEmpty())   {
            searchResultMapper.insert(insertList);
            for (int i = 0; i < insertList.size(); i++) {
                SearchResultEntity searchResultEntity = insertList.get(i);
                if (i == insertList.size()- 1){
                    searchResultEntity.setIsStop(true);
                }
                webSocketMessageSender.send(session.getId(), "NovelNameSearchResultMessageListener", JSONUtil.toJsonStr(searchResultEntity));
            }
        }else {
            webSocketMessageSender.send(session.getId(), "NovelNameSearchConsoleMessageListener", JSONUtil.toJsonStr("<== 搜索完成,未查询到"));
        }


    }

    @Override
    public String getType() {
        return "NovelNameSearchMessageListener";
    }

}
