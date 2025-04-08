package com.cat.coin.coincatmanager.controller.vo;

import com.cat.coin.coincatmanager.domain.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class DocumentConvertVo implements Serializable {
    private MultipartFile file;
    private FileType sourceType;
    private FileType targetType;


    public FileType getSourceType() {
        return sourceType;
    }

    public void setSourceType(FileType sourceType) {
        this.sourceType = sourceType;
    }

    public FileType getTargetType() {
        return targetType;
    }

    public void setTargetType(FileType targetType) {
        this.targetType = targetType;
    }

    @Override
    public String toString() {
        return "PdfConvertVo{" +
                "file=" + file +
                ", sourceType=" + sourceType +
                ", targetType=" + targetType +
                '}';
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
