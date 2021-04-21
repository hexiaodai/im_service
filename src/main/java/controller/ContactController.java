package controller;

import com.google.gson.Gson;
import model.CommunityModel;
import model.ContactModel;
import model.UserModel;
import service.CommunityService;
import service.ContactService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.List;

public class ContactController {
    // 添加好友
    public static String addFriend(Request req, Response res) {
        var contact = (new Gson()).fromJson(req.body(), ContactModel.class);

        if (contact.getOwnerEmail() == null || contact.getDstObj() == null || contact.getCate() == 0) {
            return utils.Response.Error(res, 1999, "OwnerEmail、Cate和DstObj不能为空");
        }

        var userSer = new UserService();
        var user1 = new UserModel();
        // 验证当前用户是否存在
        user1.setEmail(contact.getOwnerEmail());
        user1 = userSer.find(user1);
        if (user1.getEmail() == null) {
            return utils.Response.Error(res, 2000, "当前用户不存在");
        }
        // 验证对方是否存在
        switch (contact.getCate()) {
            case ContactModel.CONCAT_CATE_USER: {
                if (contact.getOwnerEmail().equals(contact.getDstObj())) {
                    return utils.Response.Error(res, 2001, "笨蛋！不能添加自己为好友啊");
                }

                var user2 = new UserModel();
                user2.setEmail(contact.getDstObj());
                user2 = userSer.find(user2);
                if (user2.getEmail() == null) {
                    return utils.Response.Error(res, 2002, "添加的用户不存在");
                }
                break;
            }
            case ContactModel.CONCAT_CATE_COMUNITY: {
                var commSer = new CommunityService();
                var comm = new CommunityModel();
                comm.setId(Integer.parseInt(contact.getDstObj()));
                comm = commSer.find(comm);
                if (comm.getId() == 0) {
                    return utils.Response.Error(res, 2003, "添加的群不存在");
                }
                break;
            }
        }

        // 验证是否已经是好友
        var contactSer = new ContactService();
        var result = contactSer.find(contact);
        if (result.getId() > 0) {
            return utils.Response.Error(res, 2004, "笨蛋！你们已经是好友了");
        }

        // 这里就进行双向添加好友，不需要对方确认（使用事务，随便写一下就可以了）
        if (!contactSer.addFriend(contact)) {
            return utils.Response.Error(res, 2005, "好友添加失败，请重试");
        }

        return utils.Response.Success(res, "添加好友成功");
    }

    public static boolean addFriend(ContactModel contact) {
        // 验证参数是否合法
        if (!utils.Validate.validateAddContact(contact)) {
            return false;
        }

        // 这里就进行双向添加好友，不需要对方确认（使用事务，随便写一下就可以了）
        if (!(new ContactService()).addFriend(contact)) {
            return false;
        }

        return true;
    }


    // 获取好友列表
    public static String friendList(Request req, Response res) {
        var email = req.headers("email");
        var contact = new ContactModel();
        contact.setOwnerEmail(email);
        if (contact.getOwnerEmail() == null) {
            return utils.Response.Error(res, 2000, "获取好友列表失败");
        }
        System.out.println(email);
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
            // 设置用户状态（在线 | 离线）
            if (json != null) {
                var _user = (new Gson().fromJson(json, UserModel.class));
                user.setOnline(_user.getOnline());
            }
        }
        redis.close();
        return utils.Response.Success(res, userList);
    }
//    public static String friendList(Request req, Response res) {
//        var contact = new ContactModel();
//        contact.setOwnerEmail(req.queryParams("ownerEmail"));
//        if (contact.getOwnerEmail() == null) {
//            return utils.Response.Error(res, 2000, "获取好友列表失败");
//        }
//        // 设置cate为好友类型
//        contact.setCate(ContactModel.CONCAT_CATE_USER);
//        // 查询好友关系
//        List<ContactModel> contactList = (new ContactService()).findAll(contact);
//        // 查询好友详细信息
//        var userList = (new UserService()).findByContactList(contactList);
//        // 筛选上线用户
//        var redis = utils.Redis.getJedis();
//        for (UserModel user : userList) {
//            var json = redis.get(user.getEmail());
//            // 设置用户状态（在线 | 离线）
//            if (json != null) {
//                var _user = (new Gson().fromJson(json, UserModel.class));
//                user.setOnline(_user.getOnline());
//            }
//        }
//        redis.close();
//        return utils.Response.Success(res, userList);
//    }
}
