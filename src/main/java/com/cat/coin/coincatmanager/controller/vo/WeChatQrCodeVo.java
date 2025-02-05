package com.cat.coin.coincatmanager.controller.vo;

import java.io.Serializable;

public class WeChatQrCodeVo implements Serializable {
    private String uuid;
    private String qrUrl;

    @Override
    public String toString() {
        return "WeChatQrCodeVo{" +
                "uuid='" + uuid + '\'' +
                ", qrUrl='" + qrUrl + '\'' +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}
