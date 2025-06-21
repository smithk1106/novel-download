package com.ideaflow.noveldownload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("appConfig")
public class AppConfigEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String configKey;


    private String configValue;

}