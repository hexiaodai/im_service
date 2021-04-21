package controller;

import model.MessageModel;
import service.MessageService;
import spark.Request;
import spark.Response;

import java.util.List;

public class MessageController {
    // 好友消息记录
    public static String messageList(Request req, Response res) {
        var userEmail = req.queryParams("userEmail");
        var dstObj = req.queryParams("dstObj");

        if (userEmail == null || dstObj == null) {
            return utils.Response.Error(res, 2000, "参数错误");
        }

        List<MessageModel> msgList = (new MessageService()).findFriend(userEmail, dstObj);

        return utils.Response.Success(res, msgList);
    }

    // 群消息记录
    public static String commMsgList(Request req, Response res) {
        var dstObj = req.queryParams("dstObj");
        if (dstObj == null) {
            return utils.Response.Error(res, 2000, "参数错误");
        }

        var msgM = new MessageModel();
        msgM.setDstObj(dstObj);
        List<MessageModel> msgList = (new MessageService()).findAll(msgM, "create_at");
        return utils.Response.Success(res, msgList);
    }
}
