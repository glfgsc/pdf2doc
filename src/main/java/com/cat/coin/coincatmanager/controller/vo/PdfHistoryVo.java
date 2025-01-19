package com.cat.coin.coincatmanager.controller.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import com.cat.coin.coincatmanager.domain.pojo.PageParam;

public class PdfHistoryVo   implements Serializable{
    private int id;
    private String name;
    private Timestamp createTime;
    private String newPath;
    private String oldPath;

    @Override
    public String toString() {
        return "PdfHistoryVo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", newPath='" + newPath + '\'' +
                ", oldPath='" + oldPath + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
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
