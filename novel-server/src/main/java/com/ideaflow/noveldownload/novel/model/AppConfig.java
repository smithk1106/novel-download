package com.ideaflow.noveldownload.novel.model;

import lombok.Data;


@Data
public class AppConfig {

    private String version; // 版本
    // [base]
    private String language;
    private Integer searchLimit; //搜索限制 默认20条
    private Integer sourceId; //书源id
    private String downloadPath; // # 下载路径，绝对相对均可 (Windows 路径分隔符不要用 \ ，用 \\ 或 /)
    private String extName; // 文件扩展名，支持 epub、txt、html，推荐 epub
    private Integer autoUpdate; // 启动时是否自动更新 (1 开，0 关)
    private Integer interactiveMode; //交互模式（可选值：1、2）

    // [crawl]
    private Integer preserveChapterCache; // 下载完成后是否保留章节缓存文件夹 (1 是，0 否)
    private Integer showDownloadLog;// 是否显示下载日志 (1 开，0 关)
    private Integer threads; //爬取最小间隔 (毫秒)
    private Integer minInterval; //爬取最大间隔 (毫秒)
    private Integer maxInterval; // 爬取线程数，-1 表示自动设置

    // [retry]
    private Integer maxRetryAttempts; //最大重试次数 (针对首次下载失败的章节)
    private Integer retryMinInterval; // 重试爬取最小间隔 (毫秒)
    private Integer retryMaxInterval; // 重试爬取最大间隔 (毫秒)

    private Integer proxyEnabled; // 是否启用 HTTP 代理 (1 开，0 关)
    private String proxyHost;
    private Integer proxyPort;

}