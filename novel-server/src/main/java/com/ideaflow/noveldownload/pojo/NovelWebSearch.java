package com.ideaflow.noveldownload.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class NovelWebSearch extends PageParam {
    private String name;
}
