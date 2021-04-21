package utils;

import com.google.gson.Gson;
import controller.ContactController;
import model.ContactModel;
import model.MessageModel;
import model.UserModel;
import service.ContactService;
import service.MessageService;
import service.UserService;
import ws.Chat;
import java.util.List;

public class Message {
    // 广播消息
    public static void broadcastMsg(String email, MessageModel msg) {
        // 获取我的好友记录
        var contact = new ContactModel();
        contact.setOwnerEmail(email);
        // 设置cate为好友类型
        contact.setCate(ContactModel.CONCAT_CATE_USER);
        // 查询好友关系
        List<ContactModel> contactList = (new ContactService()).findAll(contact);
        // 查询好友详细信息
        var userList = (new UserService()).findByContactList(contactList);
        // 筛选上线用户
        var redis = utils.Redis.getJedis();
        for (UserModel user : userList) {
            var json = redis.get(user.getEmail());
            if (json == null) continue;
            var friend = (new Gson().fromJson(json, UserModel.class));
            var fsess = Chat.onlineUsers.get(friend.getEmail());
            // 广播给在线好友
            if (friend.getOnline() == UserModel.ONLINE && fsess != null) {
                // 组装上线消息
                msg.setDstObj(friend.getEmail());
                try {
                    fsess.getRemote().sendString((new Gson().toJson(msg)));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        redis.close();
    }

    // 广播下线消息
    public static void broadcastOfflineMsg(String email) {
        var msg = new MessageModel();
        // 组装下线消息
        msg.setUserEmail(email);
        msg.setCmd(MessageModel.CMD_OFFLINE_MSG);
        // 广播消息给好友
        broadcastMsg(email, msg);
    }

    // 广播上线消息
    public static void broadcastOnlineMsg(String email) {
        var msg = new MessageModel();
        // 组装上线消息
        msg.setUserEmail(email);
        msg.setCmd(MessageModel.CMD_ONLINE_MSG);
        // 广播消息给好友
        broadcastMsg(email, msg);
    }

    // 离线消息使用redis list存储
    public static void pushOfflineMsg(MessageModel msg, String ...keys) {
        var msgKey = msg.getDstObj();

        // 处理消息类型
        switch (msg.getCmd()) {
            case MessageModel.CMD_SINGLE_MSG: {
                // 点对点消息，不作处理
                break;
            }
            case MessageModel.CMD_ROOM_MSG: {
                if (keys.length == 0) return;
                // 群聊（重新设置redis msg key 为用户Email）
                msgKey = keys[0];
                break;
            }
            default: {
                System.out.println("无法识别的消息类型");
                return;
            }
        }
        var redis = Redis.getJedis();
        // key: offline_msg + email
        redis.lpush(MessageModel.REDIS_OFFLINE_MSG_KEY + msgKey, (new Gson()).toJson(msg));
        redis.close();
    }

    // 发送点对点消息
    public static void sendSingleMsg(MessageModel msg) {
        var dstObj = msg.getDstObj();
        var fsess = Chat.onlineUsers.get(dstObj);
        if (fsess != null) {
            try {
                // 发送消息
                fsess.getRemote().sendString((new Gson().toJson(msg)));
                // 消息入库
                (new MessageService()).insert(msg);
                System.out.printf("【SingleMsg】%s -> %s %s\n", msg.getUserEmail(), msg.getDstObj(), msg.getContent());
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            // 离线消息存入redis
            Message.pushOfflineMsg(msg);
        }
    }

    // 发送点对点消息（dstObj: 指定接收方; saveDb: 消息是否存储数据库）
    public static void sendSingleMsg(MessageModel msg, String dstObj, Boolean saveDb) {
        var fsess = Chat.onlineUsers.get(dstObj);
        if (fsess != null) {
            try {
                // 发送消息
                fsess.getRemote().sendString((new Gson().toJson(msg)));
                // 消息入库
                if (saveDb) (new MessageService()).insert(msg);
                System.out.printf("【SingleMsg】%s -> %s %s\n", msg.getUserEmail(), dstObj, msg.getContent());
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            // 离线消息存入redis
            Message.pushOfflineMsg(msg);
        }
    }

    // 发送群消息
    public static void sendCommunityMsg(MessageModel msg, boolean saveDb) {
        try {
            // 通过群消息（id）查找群成员
            var conList = (new ContactService()).findCommunity(msg.getDstObj());
            // 获取群用户详细信息
            var userList = (new UserService()).findByContactList(conList);
            for (UserModel user : userList) {
                var fsess = Chat.onlineUsers.get(user.getEmail());
                if (fsess != null) {
                    try {
                        // 发送消息
                        fsess.getRemote().sendString((new Gson().toJson(msg)));
                        System.out.printf("【CommunityMsg】%s -> %s(%s) %s\n", msg.getUserEmail(), user.getEmail(), msg.getDstObj(), msg.getContent());
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else {
                    // redis缓存群消息，需要设置redis msg key 为群离线成员email
                    // 离线消息存入redis
                    Message.pushOfflineMsg(msg, user.getEmail());
                }
            }
            // 消息入库
            if (saveDb) (new MessageService()).insert(msg);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    // 添加好友消息
    public static void handleAddContactMsg(MessageModel msg) {
        var contact = new ContactModel();
        contact.setOwnerEmail(msg.getUserEmail());
        contact.setDstObj(msg.getDstObj());

        switch (msg.getMsgType()) {
            case MessageModel.MSG_TYPE_ADD_FRIEND: {
                contact.setCate(ContactModel.CONCAT_CATE_USER);
                if (ContactController.addFriend(contact)) {
                    msg.setContent(msg.getUserEmail() + " 添加 " + msg.getDstObj() + "为好友");
                    // 发给对方
                    sendSingleMsg(msg, msg.getDstObj(), false);
                    // 发给我自己
                    sendSingleMsg(msg, msg.getUserEmail(), false);
                }
                break;
            }
            case MessageModel.MSG_TYPE_ADD_COMUNITY: {
                contact.setCate(ContactModel.CONCAT_CATE_COMUNITY);
                if (ContactController.addFriend(contact)) {
                    msg.setContent(msg.getUserEmail() + "加入" + msg.getDstObj() + "群组");
                    // 广播给群成员
                    sendCommunityMsg(msg, false);
                }
                break;
            }
            default: {
                System.out.println("无法识别的消息类型");
                break;
            }
        }
    }
//    public static void handleAddContactMsg(MessageModel msg) {
//        var contact = new ContactModel();
//        switch (msg.getMsgType()) {
//            case MessageModel.MSG_TYPE_ADD_FRIEND: {
//                contact.setCate(ContactModel.CONCAT_CATE_USER);
//                break;
//            }
//            case MessageModel.MSG_TYPE_ADD_COMUNITY: {
//                contact.setCate(ContactModel.CONCAT_CATE_COMUNITY);
//                break;
//            }
//            default: {
//                System.out.println("无法识别（好友|群）");
//                return;
//            }
//        }
//
//        contact.setOwnerEmail(msg.getUserEmail());
//        contact.setDstObj(msg.getDstObj());
//
//        if (ContactController.addFriend(contact)) {
//            msg.setContent("添加好友成功");
//            switch (msg.getMsgType()) {
//                case MessageModel.MSG_TYPE_ADD_FRIEND: {
//                    // 发给对方
//                    sendSingleMsg(msg, msg.getDstObj(), false);
//                    // 发给我自己
//                    sendSingleMsg(msg, msg.getUserEmail(), false);
//                    break;
//                }
//                case MessageModel.MSG_TYPE_ADD_COMUNITY: {
//                    sendCommunityMsg(msg, false);
//                }
//                default: {
//                    System.out.println("无法识别的消息类型");
//                    return;
//                }
//            }
//
//        }
//    }
    // 发送离线全部消息
    public static void sendAllOfflineMsg(String email) {
        var key = MessageModel.REDIS_OFFLINE_MSG_KEY + email;
        var redis = Redis.getJedis();
        var len = redis.llen(key);
        for (int i = 0; i < len; i++) {
            var json = redis.rpop(key);
            var msg = (new Gson().fromJson(json, MessageModel.class));
            // 群离线消息是否需要存入数据库
            var saveDb = msg.getCmd() != MessageModel.CMD_ROOM_MSG;
            sendSingleMsg(msg, email, saveDb);
        }
    }
    // 发送离线消息（最近的前10离线消息）
    public static void sendOfflineMsg(String email) {
        var key = MessageModel.REDIS_OFFLINE_MSG_KEY + email;
        var start = 0;
        var stop = 10;
        var redis = Redis.getJedis();
        // 获取用户存储的最近的前10离线消息
        List<String> list = redis.lrange(key, start ,stop);
        // 由于前端已经写好了消息结构（前端不支持接收数组，偷个懒），所以这里的消息一条一条发送
        for (int i = 0; i < list.size(); i++) {
            var json = list.get(i);
            if (json == null) continue;
            var msg = (new Gson().fromJson(json, MessageModel.class));
            // 群离线消息是否需要存入数据库
            var saveDb = msg.getCmd() != MessageModel.CMD_ROOM_MSG;
            sendSingleMsg(msg, email, saveDb);
        }
        var len = redis.llen(key);
        // 移除已经推送了的离线消息（对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除）
        redis.ltrim(key, stop, len);
    }
}
