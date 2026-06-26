# Live Online 项目文档

本文档集分为“现状架构基线”和“重构计划”两个目录，避免把当前系统事实与未来演进方案混在一起。

## 文档目录

- [current-architecture/](current-architecture/): 当前项目模块、运行环境、核心业务流程。
- [refactor-plan/](refactor-plan/): 重构风险、Java 后端重构计划、生产落地复查结论。

## 初始分析方法

本次主要通过阅读构建配置、应用配置、Controller/Service/Manager/Feign/WebSocket/Go WebSocket 代码完成，并做了轻量验证：

- `live_platform` 聚合 Maven validate 当前失败，原因是父 POM 声明了不存在的 `live_platform/live_preview` 子模块。
- `live_online_chat/ChatServer` 执行 `go test ./...` 通过。
- 本机已检测到 Java 21/GraalVM、Maven 3.8.8、Go 1.23.2、Node 22/NPM 10。

## 核心业务边界

后续重构应保留现有核心业务语义：

- 用户注册、登录、资料维护、充值。
- 用户成为主播并创建直播间基础信息。
- 主播创建直播场次并开播。
- 观众进入直播间观看 WebRTC 视频流。
- 观众发送弹幕、礼物、关注、举报。
- 主播/房管/超管进行禁言、封播、举报处理。
- 主播下播后直播间在线态清理。
