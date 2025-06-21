package com.ideaflow.noveldownload.schedule;

import com.ideaflow.noveldownload.mapper.SearchResultMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteSearchResultSchedule {

    @Resource
    private SearchResultMapper searchResultMapper;

    @Scheduled(cron = "0 */20 * * * ?")
    public void run() {
        int delete = searchResultMapper.delete(null);
        log.info("searchResultMapper-删除数据:"+delete);
    }


}
