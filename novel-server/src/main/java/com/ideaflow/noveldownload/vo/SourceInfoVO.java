package com.ideaflow.noveldownload.vo;

import lombok.Data;

@Data
public class SourceInfoVO {
    private Integer id;
    private String name;
    private Boolean mainlandIp;
    private Boolean nonMainlandIp;
    private Boolean supportSearch;
    private String note;

    public SourceInfoVO(Integer id, String name, Boolean mainlandIp, Boolean nonMainlandIp, Boolean supportSearch, String note) {
        this.id = id;
        this.name = name;
        this.mainlandIp = mainlandIp;
        this.nonMainlandIp = nonMainlandIp;
        this.supportSearch = supportSearch;
        this.note = note;
    }

}