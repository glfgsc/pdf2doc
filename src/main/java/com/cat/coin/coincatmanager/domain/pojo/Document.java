package com.cat.coin.coincatmanager.domain.pojo;

import com.cat.coin.coincatmanager.domain.enums.FileType;

import java.io.Serializable;

public class Document implements Serializable {
    private String id;
    private String name;
    private String createTime;
    private String updateTime;
    private int creator;
    private FileType sourceType;
    private FileType targetType;

    public FileType getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", creator=" + creator +
                ", sourceType='" + sourceType + '\'' +
                ", targetType='" + targetType + '\'' +
                ", newPath='" + newPath + '\'' +
                ", oldPath='" + oldPath + '\'' +
                ", status=" + status +
                '}';
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    private String newPath;
    private String oldPath;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }
}
