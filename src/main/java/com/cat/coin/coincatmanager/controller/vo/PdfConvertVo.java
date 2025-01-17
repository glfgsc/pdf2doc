package com.cat.coin.coincatmanager.controller.vo;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class PdfConvertVo implements Serializable {
    private MultipartFile file;

    @Override
    public String toString() {
        return "PdfConvertVo{" +
                "file=" + file +
                '}';
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
