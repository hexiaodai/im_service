## 运行环境

- jdk 11
- mysql 8.0
- redis 6.0.9

## 构建项目

- Maven

## 框架

- sparkjava

## 用户管理 /user

### 用户登录 POST /user/login

Request

```json
{
    email: "hjm@163.com",
    password: "hjm0819"
}
```

Response 200

```json
{
    code: 0,
    msg: "",
    data: {
        id: 29,
        email: "hjm@163.com",
        uname: "原来是小民同学呀",
        avatar: "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
        createAt: 1618914288981,
        deleteTimestamp: 0,
        token: "6E3E662DFD10ADB61206342FB8922BEA",
        online: 1   
    }   
}
```

### 用户注册 POST /user/register

Request

```json
{
    email: "test2@163.com",
    password: "test2021",
    password2: "test2021",
    uname: "test123"
}
```

Response 200

```json
{
    code: 0,
    data: {
        createAt: 1618993248102,
        deleteTimestamp: 0,
        email: "test2@163.com",
        id: 0,
        online: 1,
        token: "548D7B319EF9AB24CD91FBB8031B44E1",
        uname: "test123"
    },
    msg: ""
}
```

### 搜索好友 GET /user/search_friend

Request

```json
{
    info: "hjm",
}
```

Response 200

```json
{
    code: 0,
    data: [
        0: {
            avatar: "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
            createAt: 1618914288981,
            deleteTimestamp: 0,
            email: "hjm@163.com",
            id: 29,
            online: 0,
            uname: "原来是小民同学呀",
        }
    ],
    msg: ""
}
```

### 修改用户信息 PUT /user/edit_user

Request

```json
{
    avatar: "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
    uname: "test456"
}
```

Response 200

```json
{
    code: 0,
    data: {
        avatar: "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
        createAt: 1618993248102,
        deleteTimestamp: 0,
        email: "test2@163.com",
        id: 0,
        online: 1,
        token: "548D7B319EF9AB24CD91FBB8031B44E1",
        uname: "test456",
    },
    msg: ""
}
```

获取用户信息（包括在线状态） GET /user/user_info

Request

```json
{
    email: "test@163.com"
}
```

Response 200

```json
{
    code: 0,
    data: {
        avatar: "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
        createAt: 1618847453422,
        deleteTimestamp: 0,
        email: "test@163.com",
        id: 26,
        uname: "何同学",
        online: 0
    },
    msg: ""
}
```

## 好友关系管理（好友，群） /contact

### 添加（好友、群）POST /contact/add_friend

Request

```json
{
    cmd: 15,
    dstObj: "hjm@163.com",
    msgType: 6,
    userEmail: "test2@163.com"
}
```

Response 200

```json
{
    cmd: 15,
    content: "test2@163.com 添加 hjm@163.com为好友",
    createAt: 1618994048486,
    deleteTimestamp: 0,
    dstObj: "hjm@163.com",
    msgType: 6,
    userEmail: "test2@163.com"
}
```

### 获取好友列表 GET /contact/friend_list

Request
登录成功

Response 200

```json
{
    code: 0,
    data: {
        0: {
            avatar: "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
            createAt: 1618914288981,
            deleteTimestamp: 0,
            email: "hjm@163.com",
            id: 29,
            online: 1,
            uname: "原来是小民同学呀"
        }
    },
    msg: ""
}
```

## 消息管理 /msg

### 获取好友消息列表 GET /msg/friend_msg_list

Request
登录成功

Response 200

```json
{
    code: 0,
    data: {
        0: {
            cmd: 10,
            content: "@(呵呵)",
            createAt: 1618994297941,
            deleteTimestamp: 0,
            dstObj: "hjm@163.com",
            id: 1674,
            msgType: 1,
            userEmail: "test2@163.com"
        },
        1: {
            cmd: 10,
            content: "this is a test",
            createAt: 1618994306254,
            deleteTimestamp: 0,
            dstObj: "hjm@163.com",
            id: 1675,
            msgType: 1,
            userEmail: "test2@163.com"
        }
    },
    msg: ""
}
```

获取群消息列表 GET /msg/community_msg_list

Request

```json
{
    dstObj: 1000
}
```

Response 200

```json
{
    code: 0,
    data: {
        0: {
            cmd: 11,
            content: "@(惊恐)",
            createAt: 1618994465192,
            deleteTimestamp: 0,
            dstObj: "1000",
            id: 1676,
            msgType: 1,
            userEmail: "hjm@163.com"
        },
        1: {
            cmd: 11,
            content: "hello world!",
            createAt: 1618994471825,
            deleteTimestamp: 0,
            dstObj: "1000",
            id: 1677,
            msgType: 1,
            userEmail: "hjm@163.com"
        }
    },
    msg: ""
}
```

## 群管理 /community

### 搜索群 GET /community/search_community

Request

```json
{
    info: "软件架构"
}
```

Response 200

```json
{
    code: 0,
    data: [
        0: {
            createAt: 1618847453422,
            deleteTimestamp: 0,
            icon: "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
            id: 1000,
            name: "软件架构课程交流群",
            ownerEmail: "hjm@163.com"
        }
    ],
    msg: ""
}
```

### 获取群列表 GET /community/search_community

Request
登录成功

Response 200

```json
{
    code: 0,
    data: [
        0: {
            createAt: 1618847453422,
            deleteTimestamp: 0,
            icon: "https://im-java.oss-cn-shenzhen.aliyuncs.com/images/1618985589853_5726bbf7.jpg",
            id: 1000,
            name: "软件架构课程交流群",
            ownerEmail: "hjm@163.com"
        }   
    ],
    msg: ""
}
```
