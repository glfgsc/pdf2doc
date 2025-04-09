package com.cat.coin.coincatmanager.controller.vo;

import com.cat.coin.coincatmanager.domain.pojo.PageParam;

import java.io.Serializable;
import java.util.Arrays;

public class DocumentHistoryPageVo extends PageParam implements Serializable {
    private int creator;
    private String sources;

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "DocumentHistoryPageVo{" +
                "creator=" + creator +
                ", sources='" + sources + '\'' +
                '}';
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }
}
