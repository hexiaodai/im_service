package model;

public class CommunityModel {
    private int id;
    // 群名称
    private String name;
    // 群主Email
    private String ownerEmail;
    // 群logo
    private String icon;
    // 群简介
    private String memo;
    private long createAt;
    private long deleteTimestamp;

    public CommunityModel() {
    }

    public CommunityModel(int id, String name, String ownerEmail, String icon, String memo, long createAt, long deleteTimestamp) {
        this.id = id;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.icon = icon;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getDeleteTimestamp() {
        return deleteTimestamp;
    }

    public void setDeleteTimestamp(long deleteTimestamp) {
        this.deleteTimestamp = deleteTimestamp;
    }
}
