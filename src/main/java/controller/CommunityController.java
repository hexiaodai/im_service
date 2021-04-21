package controller;

import model.CommunityModel;
import model.ContactModel;
import service.CommunityService;
import service.ContactService;
import spark.Request;
import spark.Response;

import java.util.List;

public class CommunityController {
    public static String searchCommunity(Request req, Response res) {
        var info = req.queryParams("info");

        if (info == null) {
            return utils.Response.Error(res, 2000, "参数错误");
        }
        List<CommunityModel> userList = (new CommunityService()).searchCommunity(info);

        return utils.Response.Success(res, userList);
    }

    // 获取用户加入的群详细信息
    public static String findUserCommunity(Request req, Response res) {
        var email = req.headers("email");
        var contact = new ContactModel();
        contact.setOwnerEmail(email);
        contact.setCate(ContactModel.CONCAT_CATE_COMUNITY);

        if (contact.getOwnerEmail() == null) {
            return utils.Response.Error(res, 2000, "参数错误");
        }

        // 查询加入的群
        List<ContactModel> conList = (new ContactService()).findAll(contact);

        // 获取群详细信息
        List<CommunityModel> commList = (new CommunityService()).findUserCommunityByContact(conList);

        return utils.Response.Success(res, commList);
    }
//    public static String findUserCommunity(Request req, Response res) {
//        var contact = new ContactModel();
//        contact.setOwnerEmail(req.queryParams("ownerEmail"));
//        contact.setCate(ContactModel.CONCAT_CATE_COMUNITY);
//
//        if (contact.getOwnerEmail() == null) {
//            return utils.Response.Error(res, 2000, "参数错误");
//        }
//
//        // 查询加入的群
//        List<ContactModel> conList = (new ContactService()).findAll(contact);
//
//        // 获取群详细信息
//        List<CommunityModel> commList = (new CommunityService()).findUserCommunityByContact(conList);
//
//        return utils.Response.Success(res, commList);
//    }
}
