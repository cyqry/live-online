# 项目模块地图

## 工作区分层

当前工作区不是一个统一 Git/Maven 根工程，而是多个工程并列放置：

| 路径 | 类型 | 主要职责 |
| --- | --- | --- |
| `live_platform/` | Java/Spring Cloud 多模块父工程 | 后端微服务主体：网关、账号、直播空间、WebRTC 信令、文件存储、注册中心、监控等 |
| `live_preview/` | Spring Boot + React | 前端页面源码与已构建静态资源，Spring Boot wrapper 监听 3000 |
| `live_online_chat/ChatServer/` | Go 服务 | 弹幕 WebSocket、聊天室内存状态、禁言接口 |
| `note/` | 文档/笔记 | 历史问题记录 |
| `jdk/` | 本地归档 | openjdk 压缩包，不属于源码主链路 |

## Java 后端模块

`live_platform/pom.xml` 声明 Spring Boot 3.0.11、Java 21、Spring Cloud 2022.0.2。当前声明的模块如下。

| 模块 | 端口 | 入口 | 职责 | 主要依赖 |
| --- | ---: | --- | --- | --- |
| `live_registration_center` | 7001 | `LiveRegistrationApplication` | Eureka 注册中心 | Spring Cloud Eureka Server |
| `live_gateway` | 8080 | `GatewayApplication` | HTTPS/API/WSS 统一入口，路由 `/store/**`、`/space/**`、`/account/**`、`/yws/**`，登录态校验与 `x-user` 注入 | Spring Cloud Gateway、Eureka Client、Redis Reactive、`live_common` |
| `live_account` | 8079 | `LiveAccountApplication` | 注册登录、用户资料、主播资料、充值、礼物、房管操作入口 | Spring Web、MyBatis、MySQL、Redis、OpenFeign、Eureka、`live_common` |
| `live_space` | 8081 | `LiveSpaceApplication` | 直播间基础信息、在线房间态、分类搜索、关注历史、房管/举报/封播 | Spring Web、MyBatis、MySQL、Redis/Redisson、Elasticsearch、OpenFeign、Eureka、`live_common` |
| `live_streaming` | 8078 | `LiveStreamingApplication` | WebRTC 信令 WebSocket，保存主播/观众 session，转发 offer/answer/candidate，下播清理 | Spring WebSocket、OpenFeign、Eureka、`live_common` |
| `live_store` | 8083 | `LiveStoreApplication` | base64 图片保存和读取，头像/封面/举报截图等本地文件存储 | Spring Web、Eureka、`live_common` |
| `live_admin` | 10086 | `LiveAdminApplication` | Spring Boot Admin 监控服务 | Spring Boot Admin Server、Security、Eureka |
| `live_common` | 无独立端口 | 无 | 公共模型、拦截器、Redis 序列化配置、Feign 配置、工具类、校验注解、Option/Result 类型 | Redis、Redisson、Elasticsearch、Feign、Validation |
| `live_configuration` | 无独立端口 | 无 | 常量定义：远程调用 token/header、网关 header、Redis key、礼物等 | 无 |
| `live_sso` | 未形成主业务 | `sso-* / Main` | SSO 实验骨架，目前只有 Main 类级别代码 | Spring Web、`live_common` |
| `test` | 8080 | `TestApp` | 实验/测试模块，不属于主链路 | Spring Web、`live_common` |

## 前端模块

`live_preview/` 包含两层：

- `src/main/static/`: React 17 应用源码，依赖 MUI、axios、webrtc-adapter、video.js 等。
- `src/main/java/com/ytyo/LivePreview.java`: Spring Boot wrapper，`application.properties` 配置 `server.port=3000`，`application.yaml` 配置了 SSL keystore。

前端核心职责：

- 普通 HTTP API 通过 `https://{GATEWAY_HOST}/{account|space|store}/...` 调用网关。
- 主播侧通过 `wss://{GATEWAY_HOST}/yws/webrtc_pub/{anchorId}/{roomId}` 建立 WebRTC 信令连接。
- 观众侧通过 `wss://{GATEWAY_HOST}/yws/webrtc_sub/{id}/{roomId}` 建立 WebRTC 信令连接。
- 弹幕通过 `wss://{CHAT_HOST}/chat/{roomId}/{id}` 连接 Go 聊天服务。

关键文件：

- `live_preview/src/main/static/src/Config/host.js`
- `live_preview/src/main/static/src/Api/api.js`
- `live_preview/src/main/static/src/component/Anchor/live.js`
- `live_preview/src/main/static/src/component/LiveVideo/watch.js`
- `live_preview/src/main/static/src/Api/chat.js`

## Go 聊天服务

路径：`live_online_chat/ChatServer/`

| 组件 | 说明 |
| --- | --- |
| `start.go` | gin 启动入口，监听 `:8000` |
| `Gws/myWsServer.go` | `/chat/:id/:roomId` WebSocket 升级、握手校验、心跳、消息广播 |
| `Gws/Store/*` | 聊天房间、连接、禁言状态的进程内存储 |
| `Controller/chat.go` | `/banedPost`、`/isBanedPost`、`/unBanedPost` |
| `Api/LiveSpaceApi` | 查询房间信息、生成禁言操作记录 |
| `Api/LiveAccoutApi` | 通过网关查询登录用户/主播身份 |

注意：gin 路由声明为 `/chat/:id/:roomId`，但业务解析和前端实际使用是 `/chat/{roomId}/{userId}`。当前代码靠手动解析路径维持行为，变量名和实际语义不一致。

## 当前结构问题

1. `live_platform/pom.xml` 声明了 `<module>live_preview</module>`，但实际没有 `live_platform/live_preview` 目录，聚合构建直接失败。
2. 顶层 `live_preview/pom.xml` 继承 `com.ytyo:live_platform`，默认父 POM 路径会指向工作区根目录的 `pom.xml`，而根目录不存在该文件；除非父工程已安装到本地 Maven 仓库，否则单独构建也不稳定。
3. 仓库内混有日志、jar、前端 build 产物、本地上传图片、证书文件等运行产物，源码边界不清。
4. 配置中存在硬编码数据库密码、keystore 密码、远程调用 token、TURN 账号密码等敏感信息，后续必须迁移到环境变量或密钥管理。
5. 注释和部分字符串存在明显编码污染，阅读和后续编译维护风险较高。

