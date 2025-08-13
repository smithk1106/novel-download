package com.ideaflow.noveldownload.entity;

import java.sql.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("novel")
public class NovelEntity {
    @TableId(type = IdType.AUTO)
    private Long id; //主键id
    private String name; // 小说名字
    private String cover; // 小说封面
    private String author; // 小说作者封面
    private String intro; // 小说简介
    private Integer categoryId; // 小说分类
    private Integer status; // 小说状态
    private Long wordCount; // 小说字数
    private Date lastUpdateTime; // 最后更新时间
    private String latestChapter; // 最新章节
    private String saveType; // 小说保存类型
    private String downloadUrl; // 下载地址
}