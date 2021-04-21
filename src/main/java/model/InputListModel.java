package model;

public class InputListModel {
    // 关键词
    private String info;
    // 页数
    private int pageNo;
    // 每页条数
    private int pageSize;

    public InputListModel() {
    }

    public InputListModel(String info, int pageNo, int pageSize) {
        this.info = info;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
