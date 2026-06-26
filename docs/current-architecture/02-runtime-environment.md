# 运行环境与启动基线

## 已检测到的本机工具链

| 工具 | 当前检测结果 | 项目期望 |
| --- | --- | --- |
| Java | Oracle GraalVM Java 21 | `live_platform` 父 POM 要求 Java 21 |
| Maven | 3.8.8 | 可用 |
| Go | 1.23.2 | `ChatServer/go.mod` 声明 Go 1.20 |
| Node | v22.9.0 | React 17 项目可运行，但 Node 22 对老依赖可能偏新 |
| NPM | 10.8.3 | 可用 |

## 外部中间件

| 组件 | 当前配置中的访问名 | 用途 |
| --- | --- | --- |
| MySQL 5.7 | `mysql5.7:3306` | `liveonline` 数据库，账号、主播、房间基础表、关注历史、操作记录 |
| Redis 6.0.6 | `redis6.0.6:6379` | 登录 session hash、在线直播间 `RoomDetails`、Redisson 锁 |
| Elasticsearch 7.17.3 | `elasticsearch7.17.3:9200` | 分类、房间标签、搜索。代码里使用 IK 分词相关配置，部署时需要确认 ES 插件 |
| Eureka | `registrationcenter:7001` | Java 服务注册发现 |
| Coturn/STUN/TURN | `ytycc.com:3478` 或 `localhost:3478` | WebRTC NAT 穿透 |
| TLS 证书 | JKS/keystore 文件 | 网关和前端 wrapper 配置 HTTPS/WSS |

## hosts 依赖

`live_platform/hosts_part.txt` 给出了本地 hosts 方案，核心映射如下：

```text
127.0.0.1 registrationcenter
127.0.0.1 mysql5.7
127.0.0.1 redis6.0.6
127.0.0.1 elasticsearch7.17.3
127.0.0.1 gateway
127.0.0.1 account
127.0.0.1 space
127.0.0.1 streaming
127.0.0.1 store
```

如果不改 application 配置，本地直接运行前需要把这些名字解析到本机或对应容器。

## 服务端口

| 服务 | 端口 | 协议/入口 |
| --- | ---: | --- |
| Eureka `live_registration_center` | 7001 | HTTP |
| Gateway `live_gateway` | 8080 | HTTPS，对外 REST 与 WSS 入口 |
| Account `live_account` | 8079 | HTTP，内部服务 |
| Space `live_space` | 8081 | HTTP，内部服务 |
| Streaming `live_streaming` | 8078 | WebSocket，网关 `/yws/**` 转发到此 |
| Store `live_store` | 8083 | HTTP，内部服务 |
| Admin `live_admin` | 10086 | HTTP |
| Preview Spring wrapper | 3000 | HTTPS |
| Go ChatServer | 8000 | 当前代码是 HTTP/WebSocket 监听 |

## 建议启动顺序

1. 启动中间件：MySQL、Redis、Elasticsearch、Coturn。
2. 启动 Eureka：`live_registration_center`。
3. 启动基础服务：`live_store`、`live_account`、`live_space`。
4. 启动实时链路服务：`live_streaming`。
5. 启动入口服务：`live_gateway`。
6. 启动 Go 聊天服务：`live_online_chat/ChatServer`。
7. 启动前端：React dev server 或 `live_preview` Spring wrapper。

## 当前可构建性验证

已执行：

```powershell
mvn -q -DskipTests validate
```

工作目录：`live_platform/`

结果：失败。原因是 `live_platform/pom.xml` 声明了不存在的子模块 `live_platform/live_preview`。

已执行：

```powershell
go test ./...
```

工作目录：`live_online_chat/ChatServer/`

结果：通过，现有 Go 测试包可运行。

另尝试局部编译 `live_common`，命令超时，未拿到有效结论。后续重构前应先修复 Maven 工程结构，再建立 CI 基线。

## 本地运行注意点

1. 前端 API 固定使用 `https://` 和 `wss://`，本地如果证书不可信或服务未启 TLS，会直接失败。
2. Go `ChatServer` 当前 `gin.Engine.Run(":8000")` 是明文 HTTP/WebSocket，但前端 `chat.js` 使用 `wss://localhost:8000`。本地需要 TLS 反代，或调整开发环境为 `ws://`。
3. 网关 `/yws/**` 使用 `ws://localhost:8078` 转发到 `live_streaming`，外部由网关承担 HTTPS/WSS。
4. `live_store` 的文件根路径通过当前工作目录拼接 `"/live_platform/live_store"` 得到，启动工作目录不同会影响图片读写路径。
5. Redis session 当前注释写了 TODO，`saveCookieUser` 未设置过期时间；浏览器 cookie 会过期，但 Redis hash 不会自动清理。
6. Dockerfile 依赖自定义基础镜像 `base_graal_jdk`，现有 run 脚本多为历史手工命令，不能直接视为完整部署方案。

