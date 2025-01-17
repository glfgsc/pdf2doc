package com.cat.coin.coincatmanager.domain.pojo;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Page<T> implements Serializable {

    private List<T> list;

    private Long total;

    private int nextPage;

    @Override
    public String toString() {
        return "Page{" +
                "list=" + list +
                ", total=" + total +
                ", nextPage=" + nextPage +
                '}';
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public Page() {
    }

    public Page(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public Page(List<T> list, Long total, Integer nextPage) {
        this.list = list;
        this.total = total;
        this.nextPage = nextPage;
    }
    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Page(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> Page<T> empty() {
        return new Page<>(0L);
    }

    public static <T> Page<T> empty(Long total) {
        return new Page<>(total);
    }

}
