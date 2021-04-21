package model;

import java.util.List;

public class ListModel<T> {
    private List<T> list;
    private int total;

    public ListModel() {
    }

    public ListModel(List<T> list, int total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
