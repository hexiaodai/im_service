package utils;

import com.google.gson.Gson;
import model.OutputModel;

public class Response {
    // 返回客服端一个成功的错误（code != 0）
    public static <T> String Error(spark.Response response, int code, String msg) {
        response.header("Content-type", "application/json;charset=utf-8");
        response.status(200);

        var output = new OutputModel<T>(code, msg);
        var gson = new Gson();
        return gson.toJson(output);
    }

    // 请求成功（code == 0）
    public static <T> String Success(spark.Response response, T data) {
        response.header("Content-type", "application/json;charset=utf-8");
        response.status(200);

        var output = new OutputModel<T>(0, "", data);
        var gson = new Gson();
        return gson.toJson(output);
    }
}
