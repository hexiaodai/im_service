## 湖理，Java课程期末作业 & 软件架构期末作业

## 收获
- 充分的理解 HTTP、Websocket协议之间的关系和区别
- 了解 Redis 的基本用法，并且在实际项目中使用 Redis 处理用户的离线消息
- Java课程 & 软件架构课程期末成绩拿到“优”

## 项目地址
- 前端：https://github.com/hexiaodai/im_template.git
- 后端：https://github.com/hexiaodai/im_service.git

## 开发环境

- jdk 11
- mysql 8.0
- redis 6.0.9

## 构建项目

- Maven

## 框架

- sparkjava

## 实现功能
- 权限验证
- 登录、注册
- 添加好友、群
- 修改个人信息
- 心跳检测
- 单聊
- 群聊
- 发送图片、视频、表情包
- 好友上线、下线通知
- 添加好友、群通知
- 离线消息
- 消息漫游

## websocket

```
ws://127.0.0.1:9000/chat?email=email&token=token
```

## 用户管理 /user

### 用户登录 POST /user/login

Request

```json
{
    "email": "hjm@163.com",
    "password": "hjm0819"
}
```

Response 200

```json
{
    "code": 0,
    "msg": "",
    "data": {
        "id": 29,
        "email": "hjm@163.com",
        "uname": "原来是小民同学呀",
        "avatar": "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
        "createAt": 1618914288981,
        "deleteTimestamp": 0,
        "token": "6E3E662DFD10ADB61206342FB8922BEA",
        "online": 1   
    }   
}
```

### 用户注册 POST /user/register

Request

```json
{
    "email": "test2@163.com",
    "password": "test2021",
    "password2": "test2021",
    "uname": "test123"
}
```

Response 200

```json
{
    "code": 0,
    "data": {
        "createAt": 1618993248102,
        "deleteTimestamp": 0,
        "email": "test2@163.com",
        "id": 0,
        "online": 1,
        "token": "548D7B319EF9AB24CD91FBB8031B44E1",
        "uname": "test123"
    },
    "msg": ""
}
```

### 搜索好友 GET /user/search_friend

Request

```json
{
    "info": "hjm",
}
```

Response 200

```json
{
    "code": 0,
    "data": [
        {
            "avatar": "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
            "createAt": 1618914288981,
            "deleteTimestamp": 0,
            "email": "hjm@163.com",
            "id": 29,
            "online": 0,
            "uname": "原来是小民同学呀",
        }
    ],
    "msg": ""
}
```

### 修改用户信息 PUT /user/edit_user

Request

```json
{
    "avatar": "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
    "uname": "test456"
}
```

Response 200

```json
{
    "code": 0,
    "data": {
        "avatar": "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
        "createAt": 1618993248102,
        "deleteTimestamp": 0,
        "email": "test2@163.com",
        "id": 0,
        "online": 1,
        "token": "548D7B319EF9AB24CD91FBB8031B44E1",
        "uname": "test456",
    },
    "msg": ""
}
```

获取用户信息（包括在线状态） GET /user/user_info

Request

```json
{
    "email": "test@163.com"
}
```

Response 200

```json
{
    "code": 0,
    "data": {
        "avatar": "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
        "createAt": 1618847453422,
        "deleteTimestamp": 0,
        "email": "test@163.com",
        "id": 26,
        "uname": "何同学",
        "online": 0
    },
    "msg": ""
}
```

## 好友关系管理（好友，群） /contact

### 添加（好友、群）POST /contact/add_friend

Request

```json
{
    "cmd": 15,
    "dstObj": "hjm@163.com",
    "msgType": 6,
    "userEmail": "test2@163.com"
}
```

Response 200

```json
{
    "cmd": 15,
    "content": "test2@163.com 添加 hjm@163.com为好友",
    "createAt": 1618994048486,
    "deleteTimestamp": 0,
    "dstObj": "hjm@163.com",
    "msgType": 6,
    "userEmail": "test2@163.com"
}
```

### 获取好友列表 GET /contact/friend_list

Request
登录成功

Response 200

```json
{
    "code": 0,
    "data": {
        {
            "avatar": "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
            "createAt": 1618914288981,
            "deleteTimestamp": 0,
            "email": "hjm@163.com",
            "id": 29,
            "online": 1,
            "uname": "原来是小民同学呀"
        }
    },
    "msg": ""
}
```

## 消息管理 /msg

### 获取好友消息列表 GET /msg/friend_msg_list

Request
登录成功

Response 200

```json
{
    "code": 0,
    "data": {
        {
            "cmd": 10,
            "content": "@(呵呵)",
            "createAt": 1618994297941,
            "deleteTimestamp": 0,
            "dstObj": "hjm@163.com",
            "id": 1674,
            "msgType": 1,
            "userEmail": "test2@163.com"
        },
        {
            "cmd": 10,
            "content": "this is a test",
            "createAt": 1618994306254,
            "deleteTimestamp": 0,
            "dstObj": "hjm@163.com",
            "id": 1675,
            "msgType": 1,
            "userEmail": "test2@163.com"
        }
    },
    "msg": ""
}
```

获取群消息列表 GET /msg/community_msg_list

Request

```json
{
    "dstObj": 1000
}
```

Response 200

```json
{
    "code": 0,
    "data": {
        {
            "cmd": 11,
            "content": "@(惊恐)",
            "createAt": 1618994465192,
            "deleteTimestamp": 0,
            "dstObj": "1000",
            "id": 1676,
            "msgType": 1,
            "userEmail": "hjm@163.com"
        },
        {
            "cmd": 11,
            "content": "hello world!",
            "createAt": 1618994471825,
            "deleteTimestamp": 0,
            "dstObj": "1000",
            "id": 1677,
            "msgType": 1,
            "userEmail": "hjm@163.com"
        }
    },
    "msg": ""
}
```

## 群管理 /community

### 搜索群 GET /community/search_community

Request

```json
{
    "info": "软件架构"
}
```

Response 200

```json
{
    "code": 0,
    "data": [
        {
            "createAt": 1618847453422,
            "deleteTimestamp": 0,
            "icon": "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
            "id": 1000,
            "name": "软件架构课程交流群",
            "ownerEmail": "hjm@163.com"
        }
    ],
    "msg": ""
}
```

### 获取群列表 GET /community/search_community

Request
登录成功

Response 200

```json
{
    "code": 0,
    "data": [
        {
            "createAt": 1618847453422,
            "deleteTimestamp": 0,
            "icon": "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
            "id": 1000,
            "name": "软件架构课程交流群",
            "ownerEmail": "hjm@163.com"
        }   
    ],
    "msg": ""
}
```



## 功能截图

- 权限验证

  ```java
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
  ```

- 登录、注册
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185201_f0cac64f_5174336.png "屏幕截图.png")

- 添加好友、群
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185238_68f96d90_5174336.png "屏幕截图.png")

- 修改个人信息
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185255_8460cc4c_5174336.png "屏幕截图.png")

- 心跳检测
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185340_c10b6755_5174336.png "屏幕截图.png")

- 单聊
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185352_ab14c0b3_5174336.png "屏幕截图.png")

- 群聊
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185413_5f3c684e_5174336.png "屏幕截图.png")

- 发送图片、视频、表情包
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185432_c6cf5c60_5174336.png "屏幕截图.png")
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185443_6ad55648_5174336.png "屏幕截图.png")


- 好友上线、下线通知
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185507_9d491287_5174336.png "屏幕截图.png")

- 添加好友、群通知
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185518_eafbddb4_5174336.png "屏幕截图.png")

- 离线消息
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185530_0692f227_5174336.png "屏幕截图.png")

- 消息漫游
  ![输入图片说明](https://images.gitee.com/uploads/images/2021/0421/185549_ea1b7d7a_5174336.png "屏幕截图.png")


[comment]: <> (- 登录、注册)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421170149570.png" alt="image-20210421170149570" style="zoom: 25%;" />)

[comment]: <> (- 添加好友、群)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421181313483.png" alt="image-20210421181313483" style="zoom:25%;" />)

[comment]: <> (- 修改个人信息)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421180512528.png" alt="image-20210421180512528" style="zoom:25%;" />)

[comment]: <> (- 心跳检测)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421182136725.png" alt="image-20210421182136725" style="zoom: 25%;" />)

[comment]: <> (- 单聊)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421181020090.png" alt="image-20210421181020090" style="zoom:25%;" />)

[comment]: <> (- 群聊)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421181106686.png" alt="image-20210421181106686" style="zoom:25%;" />)

[comment]: <> (- 发送图片、视频、表情包)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421170953685.png" alt="image-20210421170953685" style="zoom:25%;" />)

[comment]: <> (<img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421181822905.png" alt="image-20210421181822905" style="zoom:25%;" />)




[comment]: <> (- 好友上线、下线通知)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421181158945.png" alt="image-20210421181158945" style="zoom:25%;" />)

[comment]: <> (- 添加好友、群通知)

[comment]: <> (  ![image-20210421181313483]&#40;/Users/hjm/Library/Application Support/typora-user-images/image-20210421181313483.png&#41;)

[comment]: <> (- 离线消息)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421181506613.png" alt="image-20210421181506613" style="zoom:25%;" />)

[comment]: <> (- 消息漫游)

[comment]: <> (  <img src="/Users/hjm/Library/Application Support/typora-user-images/image-20210421181543825.png" alt="image-20210421181543825" style="zoom:25%;" />)


