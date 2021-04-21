package model;

public class UserModel {
    public static final int ONLINE = 1;
    public static final int OFFLINE = 0;

    private int id;
    private String email;
    private String password;
    private String salt;
    private String uname;
    private String avatar;
    private long createAt;
    private long deleteTimestamp;
    private String token;
    private int online;

    public UserModel() {
    }

    public UserModel(int id, String email, String password, String salt, String uname, String avatar, long createAt, long deleteTimestamp, String token, int online) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.uname = uname;
        this.avatar = avatar;
        this.createAt = createAt;
        this.deleteTimestamp = deleteTimestamp;
        this.token = token;
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }
}
