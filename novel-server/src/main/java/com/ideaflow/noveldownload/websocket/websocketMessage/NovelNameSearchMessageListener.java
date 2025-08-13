package com.ideaflow.noveldownload.websocket.websocketMessage;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import com.ideaflow.noveldownload.config.WebSocketContext;
import static com.ideaflow.noveldownload.constans.CommonConst.NOVEL_NAME_SEARCH_CONSOLE_MESSAGE_LISTENER;
import static com.ideaflow.noveldownload.constans.CommonConst.NOVEL_NAME_SEARCH_RESULT_MESSAGE_LISTENER;
import com.ideaflow.noveldownload.entity.AppConfigEntity;
import com.ideaflow.noveldownload.entity.SearchResultEntity;
import com.ideaflow.noveldownload.mapper.AppConfigMapper;
import com.ideaflow.noveldownload.mapper.SearchResultMapper;
import com.ideaflow.noveldownload.novel.context.HttpClientContext;
import com.ideaflow.noveldownload.novel.core.Crawler;
import com.ideaflow.noveldownload.novel.core.OkHttpClientFactory;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.novel.model.SearchResult;
import com.ideaflow.noveldownload.service.NovelService;
import com.ideaflow.noveldownload.websocket.websocketMessage.message.NameSearchSendMessage;
import com.ideaflow.noveldownload.websocket.websocketcore.listener.WebSocketMessageListener;
import com.ideaflow.noveldownload.websocket.websocketcore.sender.WebSocketMessageSender;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;


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

    @Resource
    private NovelService novelService;

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

        String manySourceId = config.getManySourceId();
        if (StringUtils.hasText(manySourceId)){
            for (String sourceId : manySourceId.split(",")) {
                config.setSourceId(Integer.parseInt(sourceId));
                dealSingleSourceSearch(config, session, message, isExact);
            }
        }
    }

    private void dealSingleSourceSearch(AppConfig config,WebSocketSession session, NameSearchSendMessage message, boolean isExact) {

        webSocketMessageSender.send(session.getId(), NOVEL_NAME_SEARCH_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("[i]数据源:%s", config.getSourceId())));
        // 查找数据
        List<SearchResult> results = new Crawler(config, novelService).search(message.getBookName());


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
                webSocketMessageSender.send(session.getId(), NOVEL_NAME_SEARCH_RESULT_MESSAGE_LISTENER, JSONUtil.toJsonStr(searchResultEntity));
            }
        }else {
            webSocketMessageSender.send(session.getId(), NOVEL_NAME_SEARCH_CONSOLE_MESSAGE_LISTENER, JSONUtil.toJsonStr(String.format("[i]数据源id:%s,搜索完成,未查询到",config.getSourceId())));
        }
    }

    @Override
    public String getType() {
        return "NovelNameSearchMessageListener";
    }

}
