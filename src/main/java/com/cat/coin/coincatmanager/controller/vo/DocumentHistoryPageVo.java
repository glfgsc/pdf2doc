package com.cat.coin.coincatmanager.controller.vo;

import com.cat.coin.coincatmanager.domain.pojo.PageParam;

import java.io.Serializable;

public class DocumentHistoryPageVo extends PageParam implements Serializable {
    private int creator;

    public int getCreator() {
        return creator;
    }

    @Override
    public String toString() {
        return "PdfHistoryPageVo{" +
                "creator='" + creator + '\'' +
                '}';
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }
}
