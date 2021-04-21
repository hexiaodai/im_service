package ws;

import com.google.gson.Gson;
import controller.UserController;
import model.MessageModel;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import utils.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class Chat {
    public static Map<String, Session> onlineUsers = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void connected(Session session) {
        var token = getToken(session);
        var email = getEmail(session);
        if (session.isOpen()) {
            // 保存连接用户的session
            Chat.onlineUsers.put(email, session);
            // 通知好友们，我已经上线了
            Message.broadcastOnlineMsg(email);
            // 告诉redis，我已经上线
            UserController.userOnline(email);
            // TODO 读取redis离线消息
            Message.sendAllOfflineMsg(email);
            System.out.printf("【connected】Email: %s\n", email);
        } else {
            System.out.printf("websocket连接建立失败，email=%s; token=%s\n", email, token);
            session.close();
        }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        var email = getEmail(session);
        // 告诉redis，我已经下线
        UserController.userOffLine(email);
        // 清除一下用户session
        Chat.onlineUsers.remove(getEmail(session));
        // 通知好友们，我已经下线了
        Message.broadcastOfflineMsg(email);
        System.out.printf("【closed】Email: %s\n", email);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {
        var msg = (new Gson()).fromJson(message, MessageModel.class);
        switch (msg.getCmd()) {
            // 点对点消息
            case MessageModel.CMD_SINGLE_MSG: {
                // 消息发给好友
                Message.sendSingleMsg(msg);
                // 消息发给自己，并且消息记录不需要存入数据库
                Message.sendSingleMsg(msg, getEmail(session), false);
                break;
            }
            // 群聊消息
            case MessageModel.CMD_ROOM_MSG: {
                Message.sendCommunityMsg(msg, true);
                break;
            }
            // 添加好友(群)请求消息
            case MessageModel.CMD_ADD_CONTACT_MSG: {
                Message.handleAddContactMsg(msg);
                break;
            }
            // 心跳消息
            case MessageModel.CMD_HEART: {
                // 不作处理
                System.out.printf("【heart】%s %s\n", msg.getUserEmail(), msg.getContent());
                break;
            }
            default: {
                System.out.println("【错误】无法识别的消息");
                break;
            }
        }
    }

    // 无需异常处理或权限认证，因为middleware层已经确保query参数包含合法的token、email
    public String getToken(Session session) {
        return session.getUpgradeRequest().getParameterMap().get("token").get(0);
    }
    public String getEmail(Session session) {
        return session.getUpgradeRequest().getParameterMap().get("email").get(0);
    }
}