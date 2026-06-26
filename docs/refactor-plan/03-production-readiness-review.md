# 生产落地复查结论

## 总体判断

当前重构计划方向符合生产演进目标。既然本次不需要兼容旧 cookie 和旧协议，计划可以更干净：以新 API、新 SSO、新直播/聊天协议为目标，旧实现只作为业务语义参考或底层 adapter。生产可落地的前提是：先建立可构建、可回归、可回滚的后端基线，再按能力分阶段替换。

结论：

- SSO 方向可落地，应直接采用新认证协议，不再兼容旧 `live_session`。
- VIP 权益抽象可落地，但要先做后端强制校验，不能只做前端展示。
- 支付抽象可落地，但必须先有订单、账本、幂等和回调验签，不应直接接真实支付。
- 直播 `LiveEngine` 抽象可落地，但接口最终定型前应先做 SRS 薄 PoC，避免抽象偏离 SRS 实际能力。
- Nacos 迁移可落地，但必须 profile/canary/rollback，不能一次性删除 Eureka。
- ChatService 抽象可落地，Go 可暂留为聊天引擎 adapter，但新 Java 后端不应绑定旧 Go WebSocket 协议。

## 必须修正的生产约束

### 1. Phase 0 需要加数据库迁移基线

原计划只提了 Maven、配置和 smoke test。生产落地还必须加入：

- Flyway 或 Liquibase。
- 当前表结构反向整理。
- 每次新增 VIP、支付、SSO、直播状态表都必须有版本化 migration。
- 数据迁移脚本必须可重复验证，不直接手工改库。

准入标准：

- 空库可一键初始化。
- 旧库可 dry-run migration。
- migration 失败可定位、可回滚或可补偿。

### 2. Nacos 迁移不能与业务重构耦合

Nacos 迁移是基础设施替换，应独立发布。

推荐生产路径：

1. 保留 Eureka profile。
2. 新增 Nacos profile。
3. 在测试环境全链路验证 Nacos。
4. 预发环境使用 Nacos。
5. 生产小流量/单实例 canary。
6. 全量切换。
7. 稳定观察后删除 Eureka。

注意：

- 不建议生产长期双注册，容易出现路由来源不清。
- Nacos Config 不等于密钥管理，敏感值不能明文放公共配置。
- 服务名、namespace、group、metadata 必须一次性定规范。
- Gateway `lb://` 路由和 Feign 服务名可以重新规范，但必须一次性定义命名规则并全链路验证。

### 3. SSO 采用新协议，不兼容旧 cookie

既然旧协议不是约束，SSO 应按新架构一次设计清楚。推荐 Identity 服务采用 OAuth 2.1/OIDC 语义：

- Authorization Code + PKCE。
- 短期 access token 使用 JWT。
- refresh token 使用 opaque token，服务端保存、轮转、复用检测。
- 提供 JWKS、token revoke、userinfo、logout。
- Gateway 校验 token 后注入内部 `CurrentUserContext`。
- 后端服务不解析外部 token，也不接收完整 User JSON。

实现注意：

- refresh token 不暴露给 JS，使用 HttpOnly Secure Cookie 或 BFF 托管。
- access token 生命周期短，权限变化通过短 TTL 和 revoke/blacklist 兜底。
- 服务间调用使用 mTLS 或内部签名身份，不用硬编码远程 token。
- 如果迁移存量用户，采用强制改密或一次性密码 hash 迁移，不保留明文密码路径。

### 4. VIP 必须按权益点建模

不要在业务里写：

```java
if (vipLevel >= 2) { ... }
```

生产可维护方案是：

```text
VIP 等级 -> 权益集合 -> 业务按 entitlement 判断
```

例如：

- 弹幕频率看 `CHAT_RATE_LIMIT`。
- 观看清晰度看 `LIVE_VIEW_QUALITY`。
- 礼物折扣看 `GIFT_DISCOUNT`。

这样未来增加 SVIP、活动 VIP、主播专属会员时，不需要到处改 if/else。

### 5. 支付必须先有账本和幂等

原计划里的支付抽象方向正确，但生产落地顺序要更严格：

1. 先建支付订单表。
2. 再建账本表。
3. 再建 mock provider。
4. 再接充值/VIP 履约。
5. 最后才接真实第三方支付。

真实支付前置条件：

- 回调验签。
- 回调幂等。
- 订单状态机条件更新。
- provider trade no 唯一约束。
- outbox 事件或补偿任务。
- 对账任务。
- 退款/关闭订单策略。

不能继续用覆盖 `user.property` JSON 的方式表达资产变化。

### 6. SRS 接入前要先做薄 PoC

`LiveEngine` 抽象不能只按当前 WebRTC P2P 想象。SRS 接入前必须先验证：

- publish/play URL 形态。
- WebRTC 播放地址。
- 推流鉴权方式。
- callback 事件内容。
- callback 重复、乱序、延迟处理。
- 在线人数来源。
- 断流重连语义。
- HTTPS/WSS 证书和跨域。
- SRS 单机、集群、边缘节点部署方式。

PoC 目标不是替换业务，而是反推 `LiveEngine` 接口字段，避免未来二次重构。

### 7. 当前 P2P 直播必须限流保护

当前主播端每个观众一条 PeerConnection，生产不可无限放开。

在 SRS 接入前至少要加：

- 单房间最大观众数。
- 主播端连接失败提示。
- 连接数、失败率、平均观看时长指标。
- 房间状态异常清理任务。
- 下播/封播幂等保护。

### 8. 聊天抽象不要承诺立即接管连接态

Go ChatServer 可暂留，但只能作为聊天引擎 adapter。新聊天协议由 Java/Gateway 定义，不能继续让新客户端绑定旧 Go WebSocket 路径。

Java `ChatService` 第一阶段负责：

- 禁言持久化。
- 解禁持久化。
- 房间开关同步。
- 系统消息投递接口。
- Go 服务状态同步 adapter。
- 新聊天 API/WS 协议定义。

弹幕连接、实时广播可以暂由 Go 执行，但对上层表现为 `ChatEngine` 实现。后续替换 Go 时，上层业务和新客户端协议不需要再变。

### 9. 分布式事务要用 outbox 起步

当前阶段不一定马上引入 MQ。更稳的生产路径是：

1. 先用本地事务写业务表和 outbox 表。
2. 后台任务扫描 outbox 投递。
3. 消费端按 eventId 幂等。
4. 后续再替换为 Kafka/RabbitMQ/RocketMQ。

适用场景：

- 支付成功后开通 VIP。
- 支付成功后充值到账。
- 开播后通知聊天房间。
- 下播后清理聊天房间。
- 封播后通知直播引擎和聊天服务。

## 调整后的推荐顺序

生产更稳的执行顺序如下：

| 顺序 | 阶段 | 说明 |
| --- | --- | --- |
| 1 | 构建与配置基线 | 修 Maven、配置外置、CI、migration、清理产物 |
| 2 | Nacos 基础设施迁移 | 独立 profile/canary，不混业务大改 |
| 3 | 统一响应/错误/日志/CurrentUser | 为 SSO 和后续能力铺底 |
| 4 | 新 SSO/OIDC | Authorization Code + PKCE、JWT access token、opaque refresh token |
| 5 | 安全加固 | 密码 hash、CORS、脱敏、内部调用签名 |
| 6 | VIP 权益模型 | 只做查询和后端判定，先不接支付 |
| 7 | 支付订单 + mock provider + 账本 | 先走 mock 和内部充值 |
| 8 | VIP 购买履约 | 支付成功后开通/续费 VIP |
| 9 | LiveEngine legacy adapter | 包住当前 WebRTC 实现 |
| 10 | SRS 薄 PoC | 反推接口，验证 callback/地址/鉴权 |
| 11 | SrsLiveEngine adapter | 灰度切换部分房间 |
| 12 | ChatService/ChatEngine | 定义新聊天协议，Go 仅作为当前 engine adapter |

## 阶段准入标准

每个阶段进入生产前必须满足：

- 有 migration。
- 有配置回滚方案。
- 有新接口契约和破坏性变更说明。
- 有核心自动化测试。
- 有灰度开关。
- 有监控指标。
- 有失败补偿或人工处理手册。

核心指标：

- 登录成功率、session 校验耗时、Redis 命中率。
- 支付订单创建数、支付成功数、回调重复数、履约失败数。
- VIP 激活数、过期任务耗时、权益缓存命中率。
- 开播成功率、进房成功率、下播成功率、封播成功率。
- WebRTC/SRS 连接失败率、播放首帧耗时、在线人数。
- 聊天连接数、消息投递数、禁言同步失败数。

## 对原计划的最终评价

原计划作为方向性路线是合理的，尤其是“Java 后端优先、先抽象直播/聊天能力、未来接 SRS”的边界判断是正确的。

需要修正的是落地目标：不再考虑旧 cookie/旧协议兼容，直接定义新 SSO、新 API、新信令和新聊天协议。落地节奏仍然要克制：先解决构建、配置、迁移、测试、可观测性，再做 SSO/VIP/支付/直播抽象。不能一开始就新建大量服务，也不能在没有订单账本和幂等的情况下接真实支付，更不能在未做 SRS PoC 前把直播接口定死。
