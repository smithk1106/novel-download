package com.ideaflow.noveldownload.entity;

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
    private String downloadUrl;

}