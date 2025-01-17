package com.cat.coin.coincatmanager.domain.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Pdf implements Serializable {
    private int id;
    private String name;
    private Timestamp createTime;
    private String creator;
    private String newPath;
    private String oldPath;

    @Override
    public String toString() {
        return "Pdf{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", creator='" + creator + '\'' +
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
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
