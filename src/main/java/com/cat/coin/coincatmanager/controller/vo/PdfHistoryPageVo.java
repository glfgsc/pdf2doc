package com.cat.coin.coincatmanager.controller.vo;

import com.cat.coin.coincatmanager.domain.pojo.PageParam;

import java.io.Serializable;

public class PdfHistoryPageVo extends PageParam implements Serializable {
    private String creator;

    public String getCreator() {
        return creator;
    }

    @Override
    public String toString() {
        return "PdfHistoryPageVo{" +
                "creator='" + creator + '\'' +
                '}';
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
