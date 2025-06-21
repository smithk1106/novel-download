package com.ideaflow.noveldownload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("searchResult")
public class SearchResultEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer sourceId;
    private String content;
    @TableField(exist = false)
    private Boolean isStop;

}