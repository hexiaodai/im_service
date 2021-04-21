package model;

public class MessageModel implements Cloneable {
    // 点对对点单聊，dstObg是用户邮箱
    public static final int CMD_SINGLE_MSG = 10;
    // 群聊，dstObj是群id
    public static final int CMD_ROOM_MSG = 11;
    // 系统消息
    public static final int CMD_SYS_MSG = 12;
    // 上线消息
    public static final int CMD_ONLINE_MSG = 13;
    // 下线消息
    public static final int CMD_OFFLINE_MSG = 14;
    // 添加好友（群）消息
    public static final int CMD_ADD_CONTACT_MSG = 15;
    // 心跳消息
    public static final int CMD_HEART = 0;
    public static final int CMD_ACK = 1;
    public static final int CMD_ENTRY_ROOM = 2;
    public static final int CMD_EXIT_ROOM = 3;

    // redis离线消息key
    public static final String REDIS_OFFLINE_MSG_KEY = "offline_msg_";

    // 文本样式
    public static final int MSG_TYPE_TEXT = 1;
    // 图片
    public static final int MSG_TYPE_IMG = 2;
    // 视频
    public static final int MSG_TYPE_VIDEO = 3;
    // 表情
    public static final int MSG_TYPE_EMOJI = 4;
    // 超链接
    public static final int MSG_TYPE_LIKE = 5;
    // 添加好友
    public static final int MSG_TYPE_ADD_FRIEND = 6;
    // 添加群
    public static final int MSG_TYPE_ADD_COMUNITY = 7;

    private int id;
    // 发送方邮箱
    private String userEmail;
    // 业务（广播，心跳检测，私聊，群聊）
    private int cmd;
    // 接收方（用户email，群id）
    private String dstObj;
    // 消息类型（文本、图片、语音、视频）
    private int msgType;
    // 发送的内容
    private String content;
    // 创建时间
    private long createAt;
    private long deleteTimestamp;
//    private int status;

    public MessageModel() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getDstObj() {
        return dstObj;
    }

    public void setDstObj(String dstObj) {
        this.dstObj = dstObj;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
