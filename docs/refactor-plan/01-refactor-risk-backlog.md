# 重构风险与路线建议

## 需要保留的业务不变量

后续重构不能改变这些用户可感知行为：

- 用户登录后可以创建资料、关注房间、观看历史。
- 用户可以申请主播并拥有一个直播间基础信息。
- 主播可以设置标题/封面并开播。
- 观众可以进入直播间观看、发弹幕、送礼、举报。
- 主播/房管/超管可以禁言，超管可以封播和处理举报。
- 下播或封播后在线房间不可继续观看。

## P0 风险：当前构建与配置不可控

| 风险 | 现状 | 建议 |
| --- | --- | --- |
| 聚合构建失败 | `live_platform` 父 POM 声明不存在的 `live_preview` 子模块 | 先修正 Maven 模块结构，保证 `mvn validate` 可通过 |
| 运行产物混入源码 | 仓库包含 jar、log、前端 build、上传图片、证书 | 清理目录边界，补 `.gitignore`，将样例资源和真实运行数据分离 |
| 敏感信息硬编码 | 数据库密码、keystore 密码、内部 token、TURN 密码写在源码/配置 | 用环境变量、配置中心或 secrets 管理；文档中只保留占位说明 |
| 编码污染 | 注释和部分字符串出现 mojibake | 统一源码编码，批量修复不可读注释，CI 强制 UTF-8 |
| 依赖版本混杂 | Boot 3 父工程中手动指定旧版 `spring-boot-starter-data-elasticsearch` 等 | 回归 BOM 管理，逐项验证兼容性 |

## P1 风险：安全边界薄弱

| 风险 | 影响 | 建议 |
| --- | --- | --- |
| 明文密码 | 数据泄露后不可控，无法安全审计 | 使用 BCrypt/Argon2；新增密码迁移策略 |
| Redis session 无 TTL | 退出/过期策略不一致，Redis hash 长期膨胀 | 使用独立 key + TTL 或 Redis hash field 过期替代方案 |
| `x-user` 传完整用户 JSON | 内部调用边界一旦绕过网关就可伪造身份 | 改为签名 token 或只传 userId，由服务端查询可信身份 |
| 远程调用 token 硬编码 | 服务间鉴权弱，泄露后无法轮换 | 使用 mTLS、网关内网 ACL 或可轮换服务 token |
| Redis `activateDefaultTyping` | Jackson 默认类型信息存在反序列化攻击面 | 改为白名单类型序列化或 JSON DTO |
| Go 客户端跳过 TLS 校验 | 中间人攻击风险 | 修复证书链，不使用 `InsecureSkipVerify` |
| 身份认证函数返回 true | 实名/身份校验形同虚设 | 接入真实认证或明确标记为 mock profile |

## P2 风险：直播高并发架构瓶颈

当前媒体路径是浏览器 P2P：

- 观众每进入一次，主播端创建一条 `RTCPeerConnection`。
- 主播本地媒体流被添加到每个连接。
- 单主播上行带宽随观众数线性增长。

这适合 Demo 或极低并发，不适合高并发直播。建议路线：

1. 短期：保留现业务接口，给当前 P2P 模式加上并发限制、错误提示和指标。
2. 中期：引入 SFU/媒体服务器，例如 LiveKit、mediasoup、Janus、SRS WebRTC。
3. 长期：把 `live_streaming` 改为“房间信令与鉴权服务”，媒体转发交给 SFU，房间状态通过事件驱动同步。

## P3 风险：在线状态不可横向扩展

| 位置 | 当前状态 | 高并发问题 | 建议 |
| --- | --- | --- | --- |
| `live_streaming` | `CopyOnWriteArrayList<RoomWebRtcStore>` + 本地 session | 多实例不共享，网关无明确 sticky，会找不到主播 session | 统一房间 session registry；网关 sticky；或信令服务无状态化 + SFU |
| `live_space` | Redis hash 存整个 `RoomDetails`，全局 Redisson 读写锁 `lockkey` | 所有房间进入/离开串行化，`CopyOnWriteArrayList` 不适合频繁写 | 用 Redis Set/ZSet 维护成员，按 roomId 加锁或 Lua 原子操作 |
| `ChatServer` | Go 进程内 rooms/banPostList | 重启丢失，多实例互相不可见 | Redis Pub/Sub、Stream、Kafka 或专门 IM 服务 |
| `ReportService` | Java 进程内 `ConcurrentHashMap` | 重启丢失，多实例不一致 | 持久化到 MySQL/Redis，按状态流转 |

## P4 风险：领域模型和事务不清

需要优先梳理的聚合边界：

- 用户账号：登录凭证、用户资料、资产余额。
- 主播身份：主播资料、房间归属。
- 直播间基础信息：`room_info`、分类、标签。
- 在线直播场次：标题、封面、在线人数、状态。
- 互动：弹幕、禁言、礼物、举报、操作记录。

建议将“房间基础信息”和“在线直播场次”拆开，建立清晰状态机：

```text
CREATED -> LIVE_PREPARING -> LIVE -> OFFLINE
                 |             |
                 v             v
              BLOCKED <----- FORCE_OFFLINE
```

礼物应改为账本模型：

- 每次送礼有 requestId，保证幂等。
- 用户扣款、主播收入、礼物展示事件分开记录。
- 展示失败不影响账务成功。
- 账务成功后通过消息事件通知前端。

主播注册应改为可恢复流程：

- 更新用户实名。
- 创建主播资料。
- 创建房间基础信息。
- 创建 ES 标签。
- 任一步失败后可补偿或重试，不依赖同步 HTTP 手动回滚。

## P5 风险：API 与代码规范

| 问题 | 建议 |
| --- | --- |
| `ResponseEntity<?>` + 字符串错误散落 | 定义统一响应体和错误码 |
| form/json 混用且接口命名不一致 | 明确 REST 规范；保留旧接口时增加 adapter |
| `banedPost` 等拼写错误进入 API | 新增正确 API，旧 API 标记 deprecated |
| Controller 里做大量业务编排 | 下沉到 application service，Controller 只做协议适配 |
| `System.out.println` 和乱码日志 | 用结构化日志，补 traceId/roomId/userId |
| 直接 path 拼接读写文件 | 做路径归一化，限制根目录，校验文件类型和大小 |
| 测试薄弱 | 先补核心服务单元测试，再补契约测试和 E2E |

## 建议分阶段推进

### 第一阶段：建立可运行基线

- 修正 Maven 模块结构，保证聚合 `validate/test/package` 可运行。
- 从源码中剥离日志、jar、上传图片、构建产物。
- 建立 `.env.example` 或 profile 配置，敏感值全部外置。
- 固定本地启动脚本：中间件、Java 服务、Go 服务、前端。
- 补最小 CI：Java compile/test、Go test、前端 build。

### 第二阶段：不改业务的结构整理

- 统一响应模型、错误码、日志字段。
- 把公共鉴权和 session 模型梳理清楚，减少 `x-user` JSON 传递。
- 将房间在线状态的数据结构从整个对象 hash 改为 Redis Set/Hash 分字段。
- 给开播、进房、下播、封播建立集成测试。

### 第三阶段：直播链路升级

- 先给 P2P 模式加观众数限制与指标。
- 选型 SFU/媒体服务器并做旁路 PoC。
- 保持现有前端业务页面，替换底层信令协议适配层。
- 将信令服务改为可水平扩展，房间状态事件化。

### 第四阶段：互动与资产可靠性

- 弹幕状态迁移到可扩展消息通道。
- 禁言、举报持久化。
- 礼物账务改为 ledger + 幂等 + 事件通知。
- 增加压测：开播、进房、弹幕、礼物、下播。

