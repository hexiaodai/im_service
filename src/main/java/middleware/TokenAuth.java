package middleware;

import com.google.gson.Gson;
import model.UserModel;
import spark.Request;
import spark.Response;

import java.util.Arrays;

import static spark.Spark.halt;

public class TokenAuth {
    // api白名单
    public static String[] allowPath = new String[] {"user/login", "user/register"};

    public static void tokenAuthMiddleware(Request req, Response res) {
        if (Arrays.asList(TokenAuth.allowPath).contains(req.splat()[0])) return;

        // 得到header或query上的email和token
        var email = getArg(req, "email");
        var token = getArg(req, "token");

        // 若header和query都不存在email和token，直接401
        if (token == null || email == null) halt(401, "未经授权的请求");
        var redis = utils.Redis.getJedis();
        // 通过email拿到redis用户信息
        var json = redis.get(email);
        redis.close();
        if (json == null) halt(401, "未经授权的请求");
        var user = (new Gson().fromJson(json, UserModel.class));
        // 验证token是否合法
        if (!user.getToken().equals(token)) halt(401, "未经授权的请求");
    }

    private static String getArg(Request req, String key) {
        // 得到header上的email和token
        var val = req.headers(key);
        // 若header上不存在，则去query上获取
        if (val == null) {
            val = req.queryParams(key);
        }
        return val;
    }
}
