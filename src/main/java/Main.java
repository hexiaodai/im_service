import static spark.Spark.*;

import controller.*;
import middleware.*;
import ws.Chat;

public class Main {
    public static void main(String[] args) {
        port(9000);

        // webSocket
        webSocket("/chat", Chat.class);

        // 请求之前做点什么？（鉴权）
        before("/*", (req, res) -> TokenAuth.tokenAuthMiddleware(req, res));

        path("/", () -> {
            // 用户管理
            path("/user", () -> {
                // 登录接口
                post("/login", (req, res) -> UserController.login(req, res));
                // 注册接口
                post("/register", (req, res) -> UserController.register(req, res));
                // 搜索好友
                get("/search_friend", (req, res) -> UserController.searchFriend(req, res));
                // 修改用户信息
                put("/edit_user", (req, res) -> UserController.editUser(req, res));
                // 获取用户信息（包括在线状态）
                get("/user_info", (req, res) -> UserController.userInfo(req, res));
            });

            // 好友关系管理（好友，群）
            path("/contact", () -> {
                // 添加（好友、群）
                post("/add_friend", (req, res) -> ContactController.addFriend(req, res));
                // 获取好友列表
                get("/friend_list", (req, res) -> ContactController.friendList(req, res));
            });

            // 消息管理
            path("/msg", () -> {
                // 获取好友消息列表
                get("/friend_msg_list", (req, res) -> MessageController.messageList(req, res));
                // 获取群消息列表
                get("/community_msg_list", (req, res) -> MessageController.commMsgList(req, res));
            });

            // 群管理
            path("/community", () -> {
                // 搜索群
                get("/search_community", (req, res) -> CommunityController.searchCommunity(req, res));
                // 获取群列表
                get("/community_list", (req, res) -> CommunityController.findUserCommunity(req, res));
            });
        });
    }
}
