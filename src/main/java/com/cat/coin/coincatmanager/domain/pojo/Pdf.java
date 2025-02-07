package com.cat.coin.coincatmanager.domain.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Pdf implements Serializable {
    private String id;
    private String name;
    private String createTime;
    private String updateTime;
    private int creator;

    public String getUpdateTime() {
        return updateTime;
    }

    @Override
    public String toString() {
        return "Pdf{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", creator='" + creator + '\'' +
                ", newPath='" + newPath + '\'' +
                ", oldPath='" + oldPath + '\'' +
                ", status=" + status +
                '}';
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
