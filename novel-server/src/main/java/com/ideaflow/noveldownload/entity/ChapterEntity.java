package com.ideaflow.noveldownload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("chapter")
public class ChapterEntity {
    @TableId(type = IdType.AUTO)
    private Long id; //主键id
    private Long bookId; // 小说id
    private String title; // 章节名
    private Long order1; // 章节顺序
    private Long wordCount; // 字数
    private String content; // 章节内容
}