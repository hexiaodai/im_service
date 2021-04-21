package model;

public class ContactModel {
    // 好友
    public static final int CONCAT_CATE_USER = 1;
    // 群
    public static final int CONCAT_CATE_COMUNITY = 2;

    private int id;
    // 添加端的Email
    private String ownerEmail;
    // 被添加端的Email
    private String dstObj;
    // 区分 好友 | 群
    private int cate;
    // 简介
    private String memo;
    private String createAt;
    private int deleteTimestamp;

    public ContactModel() {
    }

    public ContactModel(int id, String ownerEmail, String dstObj, int cate, String memo, String createAt, int deleteTimestamp) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.dstObj = dstObj;
        this.cate = cate;
        this.memo = memo;
        this.createAt = createAt;
        this.deleteTimestamp = deleteTimestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getDstObj() {
        return dstObj;
    }

    public void setDstObj(String dstObj) {
        this.dstObj = dstObj;
    }

    public int getCate() {
        return cate;
    }

    public void setCate(int cate) {
        this.cate = cate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getDeleteTimestamp() {
        return deleteTimestamp;
    }

    public void setDeleteTimestamp(int deleteTimestamp) {
        this.deleteTimestamp = deleteTimestamp;
    }
}
