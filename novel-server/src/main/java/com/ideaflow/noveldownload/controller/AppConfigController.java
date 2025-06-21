package com.ideaflow.noveldownload.controller;

import cn.hutool.json.JSONUtil;
import com.ideaflow.noveldownload.entity.AppConfigEntity;
import com.ideaflow.noveldownload.mapper.AppConfigMapper;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import com.ideaflow.noveldownload.pojo.CommonResult;
import com.ideaflow.noveldownload.vo.SourceInfoVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class AppConfigController {

    @jakarta.annotation.Resource
    private AppConfigMapper appConfigMapper;

    @GetMapping("/getConfig")
    public CommonResult<AppConfig> getConfig() {
        // 检查配置是否存在
        AppConfigEntity existConfig = appConfigMapper.selectById(1);
        AppConfig appConfig = JSONUtil.toBean(existConfig.getConfigValue(), AppConfig.class, true);
        return CommonResult.success(appConfig);
    }
    @GetMapping("/getSourceInfo")
    public CommonResult<List<SourceInfoVO>> getSourceInfo() {
        // 这里直接硬编码返回，实际可从配置或数据库读取
        List<SourceInfoVO> sourceInfoVOS = List.of(
                new SourceInfoVO(1, "香书小说", true, false, true, ""),
                new SourceInfoVO(2, "书海阁小说网", true, true, true, "搜索限流 (Unexpected end of file from server)"),
                new SourceInfoVO(3, "梦书中文", true, false, true, "搜索限流 (Connect timed out)"),
                new SourceInfoVO(4, "鸟书网", true, false, true, "搜索限流 (Read timed out)，数量15w+"),
                new SourceInfoVO(5, "新天禧小说", true, false, true, "下载过快会导致章节内容为空，建议线程数不大于5"),
                new SourceInfoVO(7, "69书吧1", false, true, false, "需要梯子，搜索有CF，章节限流，推荐线程数1"),
                new SourceInfoVO(8, "大熊猫文学", true, true, true, ""),
                new SourceInfoVO(9, "笔趣阁22", true, true, true, ""),
                new SourceInfoVO(10, "笔尖中文", true, false, true, ""),
                new SourceInfoVO(11, "零点小说", true, true, true, "限流程度和69书吧相似，爬取过快会封IP且获取不到正文"),
                new SourceInfoVO(12, "得奇小说网", false, true, true, "基本只有新书，爬取频率过快会封禁IP (Remote host terminated the handshake)，搜索有限流 (Connection reset)"),
                new SourceInfoVO(13, "小说虎", true, true, true, "正文广告较多，需手动过滤"),
                new SourceInfoVO(14, "略更网", true, true, true, ""),
                new SourceInfoVO(15, "书林文学", true, true, true, "源站目录有重复、缺章的情况，章节限流"),
                new SourceInfoVO(16, "速读谷", false, true, true, "同得奇小说网"),
                new SourceInfoVO(17, "八一中文网", true, true, true, ""),
                new SourceInfoVO(18, "悠久小说网", true, false, true, ""),
                new SourceInfoVO(19, "阅读库", true, true, true, ""),
                new SourceInfoVO(20, "顶点小说", true, true, true, "搜索、详情、章节限流"));
        return CommonResult.success(sourceInfoVOS);
    }

    @PostMapping("/updateConfig")
    public CommonResult<Void> updateConfig(@Valid @RequestBody AppConfig appConfig) {

        
        // 检查配置是否存在
        AppConfigEntity existConfig = appConfigMapper.selectById(1);

        
        // 创建实体对象并复制属性
        existConfig.setConfigValue(JSONUtil.toJsonStr(appConfig));
        
        // 更新配置
        int rows = appConfigMapper.updateById(existConfig);
        
        if (rows > 0) {
            return CommonResult.success();
        } else {
            return CommonResult.error("更新配置失败");
        }
    }
}
