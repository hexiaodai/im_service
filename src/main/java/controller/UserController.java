package controller;

import com.google.gson.Gson;
import model.UserModel;
import service.UserService;
import spark.Request;
import spark.Response;
import utils.Rand;
import utils.Redis;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    // 用户登录
    public static String login(Request req, Response res) {
        var body = (new Gson()).fromJson(req.body(), UserModel.class);
        // 检测Email和Password是否为空
        if (body.getEmail() == null || body.getPassword() == null) {
            return utils.Response.Error(res, 2000, "Email或Password不能为空");
        }
        var user = new UserModel();
        user.setEmail(body.getEmail());
        user = (new UserService()).find(user);
        // 检测用户是否存在
        if (user.getEmail() == null) {
            return utils.Response.Error(res, 2001, "用户不存在");
        }

        // 检测密码是否匹配
        if (!utils.Validate.validatePasswdEquals(user.getPassword(), body.getPassword(), user.getSalt())) {
            return utils.Response.Error(res, 2002, "密码错误");
        }

        // 移除redis中的旧用户信息
        var redis = utils.Redis.getJedis();
        redis.del(user.getEmail());
        // 刷新token
        user.setToken(Rand.md5Encode(String.valueOf(System.nanoTime())));
        user.setOnline(UserModel.ONLINE);
        // 存入新用户信息到redis
        redis.set(user.getEmail(), (new Gson()).toJson(user));
        redis.close();

        // 屏蔽一下隐私字段（随便处理一下）
        return utils.Response.Success(res, shieldPrivate(user));
    }

    // 用户注册
    public static String register(Request req, Response res) {
        var body = (new Gson()).fromJson(req.body(), UserModel.class);

        // 检测Email和Password是否为空
        if (body.getEmail() == null || body.getPassword() == null) {
            return utils.Response.Error(res, 1999, "Email或Password不能为空");
        }

        // 检测Email是否合法
        if (!utils.Validate.validateEmail(body.getEmail())) {
            return utils.Response.Error(res, 2000, "Email格式不合法");
        }

        // 验证password是否合法（由数字和字母组成，并且要同时含有数字和字母，且长度要在6-16位之间）
        if (!utils.Validate.validatePasswd(body.getPassword())) {
            return utils.Response.Error(res, 2001, "Password格式不正确（密码由数字和字母组成，并且要同时含有数字和字母，且长度要在6-16位之间）");
        }

        var user = (new UserService()).find(body);
        // 检测Email是否占用
        if (user.getEmail() != null) {
            return utils.Response.Error(res, 2002, "Email已被注册");
        }

        user.setEmail(body.getEmail());
        user.setUname(body.getUname());
        // 生成随机字符，长度为6
        user.setSalt(Rand.randChars(6));
        // 密码 + 盐 => md5加密
        user.setPassword(Rand.makePassword(body.getPassword(), user.getSalt()));
        user.setCreateAt(System.currentTimeMillis());
        // 入库
        if (!(new UserService()).insert(user)) {
            return utils.Response.Error(res, 2003, "意料之外的错误，请重新尝试");
        }

        // 生成token（这里就随便生成一个时间戳，转成32位MD5）
        user.setToken(Rand.md5Encode(String.valueOf(System.nanoTime())));
        user.setOnline(UserModel.ONLINE);
        var redis = utils.Redis.getJedis();
        redis.set(user.getEmail(), (new Gson()).toJson(user));
        redis.close();
        // 屏蔽一下隐私字段（随便处理一下）
        return utils.Response.Success(res, shieldPrivate(user));
    }

    public static String userInfo(Request req, Response res) {
        var email = req.queryParams("email");
        if (email == null)
            return utils.Response.Error(res, 2000, "参数错误");

        // 从redis中读取用户信息
        var redis = utils.Redis.getJedis();
        var json = redis.get(email);
        redis.close();
        var user = new UserModel();
        if (json != null) {
            user = (new Gson().fromJson(json, UserModel.class));
        } else {
            user.setEmail(email);
            user = (new UserService().find(user));
        }

        if (user.getEmail() == null)
            return utils.Response.Error(res, 2000, "查找的用户不存在");
        return utils.Response.Success(res, shieldPrivate(user));
    }

    // 搜索好友
    public static String searchFriend(Request req, Response res) {
        var info = req.queryParams("info");

        if (info == null)
            return utils.Response.Error(res, 2000, "参数错误");

        List<UserModel> userList = new ArrayList<>();
        userList = (new UserService()).searchFriend(info);

        return utils.Response.Success(res, userList);
    }

    // 编辑用户信息（仅支持修改头像和用户名）
    public static String editUser(Request req, Response res) {
        var body = (new Gson()).fromJson(req.body(), UserModel.class);
        var email = req.headers("email");
        body.setEmail(email);
        if (body.getUname() == null || body.getAvatar() == null) {
            return utils.Response.Error(res, 1999, "请输入完整的用户名和头像");
        }

        var redis = Redis.getJedis();
        var json = redis.get(body.getEmail());
        if (json == null) {
            return utils.Response.Error(res, 2000, "用户获取失败，请重新登录");
        }

        var user = (new Gson()).fromJson(json, UserModel.class);
        user.setUname(body.getUname());
        user.setAvatar(body.getAvatar());
        // 更新用户信息
        if (!(new UserService()).update(user)) {
            return utils.Response.Error(res, 2001, "意料之外的错误，更新用户信息失败");
        }
        redis.set(user.getEmail(), (new Gson()).toJson(user));
        redis.close();
        return utils.Response.Success(res, shieldPrivate(user));
    }
    // public static String editUser(Request req, Response res) {
    // var body = (new Gson()).fromJson(req.body(), UserModel.class);
    // // 检测参数是否为空
    // if (body.getToken() == null || body.getEmail() == null) {
    // return utils.Response.Error(res, 1999, "Email不能为空，或用户未登录");
    // }
    //
    // var redis = Redis.getJedis();
    // var user = (new Gson()).fromJson(redis.get(body.getEmail()),
    // UserModel.class);
    // if (user.getEmail() == null) {
    // return utils.Response.Error(res, 2000, "用户未登录");
    // }
    //
    // // 检测token是否合法
    // if (!user.getToken().equals(body.getToken())) {
    // return utils.Response.Error(res, 2001, "非法凭证");
    // }
    //
    // user.setUname(body.getUname());
    // user.setAvatar(body.getAvatar());
    //
    // // 更新用户信息
    // var userSer = new UserService();
    // if (!userSer.update(user)) {
    // return utils.Response.Error(res, 2002, "用户未登录");
    // }
    // redis.set(user.getEmail(), (new Gson()).toJson(user));
    // redis.close();
    // return utils.Response.Success(res, shieldPrivate(user));
    // }

    // 与会下线
    public static void userOffLine(String email) {
        var redis = utils.Redis.getJedis();
        var json = redis.get(email);
        if (json != null) {
            var user = (new Gson().fromJson(json, UserModel.class));
            // 告诉redis，用户已经下线
            user.setOnline(UserModel.OFFLINE);
            redis.set(user.getEmail(), (new Gson().toJson(user)));
        }
        redis.close();
    }

    // 用户上线
    public static void userOnline(String email) {
        var redis = utils.Redis.getJedis();
        var json = redis.get(email);
        if (json != null) {
            var user = (new Gson().fromJson(json, UserModel.class));
            // 告诉redis，用户已经下线
            user.setOnline(UserModel.ONLINE);
            redis.set(user.getEmail(), (new Gson().toJson(user)));
        }
        redis.close();
    }

    // 随便屏蔽一下隐私字段（随便处理一下）
    private static UserModel shieldPrivate(UserModel userModel) {
        userModel.setPassword(null);
        userModel.setSalt(null);
        return userModel;
    }
}
