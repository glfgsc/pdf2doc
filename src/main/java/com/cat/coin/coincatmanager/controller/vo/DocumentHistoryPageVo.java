package com.cat.coin.coincatmanager.controller.vo;

import com.cat.coin.coincatmanager.domain.enums.FileType;
import com.cat.coin.coincatmanager.domain.pojo.PageParam;

import java.io.Serializable;
import java.util.Arrays;

public class DocumentHistoryPageVo extends PageParam implements Serializable {
    private int creator;
    private String sources;
    private String name;
    private String targetType;
    private Integer status;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DocumentHistoryPageVo{" +
                "creator=" + creator +
                ", sources='" + sources + '\'' +
                ", name='" + name + '\'' +
                ", targetType=" + targetType +
                ", status=" + status +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }
}
