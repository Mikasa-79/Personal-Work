# SOLID 检查清单


## 1. 待审查的 AI 设计基线

AI 生成的 P3 类图采用以下结构：

- 领域实体：`User`、`UserProfile`、`HelpRequest`、`Order`、`Review`、`Notification`、`AuditLog`
- 应用服务：`AuthService`、`RequestService`、`OrderService`、`ReviewService`、`NotificationService`
- 控制器：`AuthController`、`RequestController`、`OrderController`、`ReviewController`、`NotificationController`
- 数据访问：`UserMapper`、`RequestMapper`、`OrderMapper`、`ReviewMapper`、`NotificationMapper`
- 独立规则对象：`OrderStateMachine`、`CreditPolicy`

需要重点人工审查的设计点：

1. `OrderService` 是否承担了过多业务职责。
2. `RequestService` 是否把发布、查询、审核混在同一个类中导致职责过宽。
3. 需求分类和订单状态扩展时，是否需要大面积修改既有代码。
4. `CreditPolicy` 是否足以隔离信用分规则变化。
5. Controller 是否直接依赖 Mapper 或实体对象。
6. 前端 API 层是否避免页面直接调用原始 Axios。

## 2. SOLID 逐条检查表

| SOLID 原则 | 检查问题 | AI 设计是否违反 | 违反说明 | 修正方案 |
|-----------|---------|----------------|----------|----------|
| S - 单一职责 | 有没有类承担了过多职责？例如一个 Service 同时处理认证、订单、通知和评价。 | **是** — `RequestService` 同时包含面向普通用户的 `publish`/`query`/`getDetail` 方法和面向管理员的 `audit` 方法，将两种不同 actor 的职责混入同一个类。此外 `OrderService` 直接依赖 `RequestMapper`，暗示其可能承担了需求状态管理的额外职责。 | `RequestService` 违反单一职责：用户操作（发布、浏览、查看详情）与管理员操作（审核需求）属于不同 actor 的职责边界。当管理员审核逻辑变更（如增加多级审核、审核备注模板）时，会影响用户端的发布/查询代码路径，增加回归风险。<br><br>`OrderService` 直接操作 `RequestMapper` 来处理需求状态（如接单时 LOCK 需求），这隐式地将需求状态管理职责分散到了订单服务中。 | 1. 从 `RequestService` 中移除 `audit` 方法，将其移至已有的 `AdminService`（详细设计文档第4节表格中已列出 `AdminService` 但类图中未体现其与 `RequestService` 的关系）。<br>2. `OrderService` 应通过 `RequestService` 修改需求状态（如 `RequestService.lockRequest(id)`），而非直接调用 `RequestMapper`，以保证需求状态变更只有一个入口。 |
| O - 开闭原则 | 新增需求类型、通知类型或信用分规则时，是否需要修改大量已有代码？ | **部分违反** — `CreditPolicy` 和 `OrderStateMachine` 设计良好，信用分规则和订单状态扩展无需修改 Service；但 `NotificationService` 的 `sendOrderChanged(Order)` / `sendReviewCreated(Review)` 方法按事件类型硬编码，新增通知类型（如 `ORDER_CANCELLED`）需要修改 `NotificationService` 类本身。同时新增 `RequestCategory` 需同步修改 Java 枚举和数据库 CHECK 约束。 | `NotificationService` 对扩展未封闭：当前设计为每种通知事件编写独立方法，新增事件类型时需要新增方法 + 新增枚举值 + 修改 CHECK 约束，三处同步修改容易遗漏。<br><br>`RequestCategory` 枚举和 CHECK 约束的修改属于配置层变更，Service 层的分类筛选逻辑（`query` 方法通过参数化查询按 category 过滤）无需改动，因此该部分风险较低。<br><br>`CreditPolicy` 和 `OrderStateMachine` 设计符合开闭原则：新增信用分规则只需替换/扩展策略实现，新增订单状态只需扩展状态机规则表。 | 1. 将 `NotificationService` 的通知发送重构为事件驱动：定义 `NotificationEvent` 接口（含 `getType()`、`getReceiverId()`、`getTitle()`、`getContent()` 方法），`NotificationService.send(NotificationEvent event)` 统一处理，新增通知类型只需新增事件类实现该接口。<br>2. 对于枚举和 CHECK 约束的同步问题，在 Flyway 迁移脚本中统一管理，并在 CI 中增加枚举值与数据库 CHECK 约束的一致性检查。 |
| L - 里氏替换 | 如果后续为通知、信用策略、订单状态增加子类或实现类，是否能替换父类/接口使用？ | **设计准备不足** — 当前设计中 `OrderStateMachine` 和 `CreditPolicy` 均为具体类，未定义接口。`ReviewService` 和 `OrderService` 直接依赖具体类而非抽象。若后续需要 `StrictCreditPolicy` / `LenientCreditPolicy` 或不同类型的订单状态机，需要先重构 Service 使其依赖接口，再引入子类。 | 这不是直接的里氏替换违规（当前无继承体系），但属于"未为扩展准备抽象"的设计债务。如果 P1/P2 阶段需要不同信用分策略或状态流转规则变体，必须先修改 Service 的依赖声明，属于"为扩展而修改既有代码"。<br><br>好的一面是 `CreditPolicy` 和 `OrderStateMachine` 已作为独立类从 Service 中分离，只需抽取接口即可获得完整可替换性——改动范围可控。 | 1. 为 `CreditPolicy` 抽取 `ICreditPolicy` 接口，让 `ReviewService` 依赖接口而非具体类。<br>2. 为 `OrderStateMachine` 抽取 `IOrderStateMachine` 接口，让 `OrderService` 依赖接口。<br>3. 在 Spring 容器中通过 `@Primary` 标注默认实现，后续新增实现类只需加 `@Qualifier` 注入。 |
| I - 接口隔离 | 是否存在过大的接口，例如一个 `CampusHubService` 暴露所有用户、需求、订单、评价方法？ | **否** — AI 设计已将服务按业务模块拆分为 `AuthService`、`RequestService`、`OrderService`、`ReviewService`、`NotificationService`，每个 Controller 只依赖其对应的 Service，不存在"胖接口"问题。前端 API 层同样按模块拆分为 `AuthApi`、`RequestApi`、`OrderApi`、`ReviewApi`，页面组件不直接依赖原始 Axios。 | 无需修正。此原则在设计基线中已得到较好遵守，Service 和 API 层的模块边界清晰。 | 保持现有设计。后续新增功能（如聊天、支付）时应继续遵循"一个模块一个 Service/API"的拆分方式，不将新功能塞入已有 Service。 |
| D - 依赖倒转 | 高层模块是否直接依赖低层实现，例如 Controller 直接调用 Mapper，或 Service 直接拼 SQL？ | **是** — 存在三处依赖倒转问题：<br>1. Controller 层直接依赖具体 Service 类（类图中实线箭头指向具体类而非接口），如 `AuthController --> AuthService`。<br>2. `OrderService` 直接依赖 `RequestMapper`（低层数据访问），跨模块绕过了 `RequestService` 的业务规则。<br>3. `ReviewService` 直接依赖具体类 `NotificationService`，未通过接口解耦。 | **问题1：Controller → 具体 Service**：虽然没有直接的"Controller 调 Mapper"问题，但 Controller 依赖具体 Service 类使得单元测试时无法 mock Service，也降低了 Service 实现的可替换性。<br><br>**问题2：OrderService → RequestMapper**：这是更严重的问题。`OrderService` 在接单时需要将需求状态改为 `LOCKED`，如果直接操作 `RequestMapper`，则绕过了 `RequestService` 中可能的业务校验（如权限检查、审核状态判断），导致需求状态可能被非法修改。<br><br>**问题3：ReviewService → NotificationService**：评价完成后需要发送通知，但直接依赖具体类使得两个 Service 强耦合，未来切换通知方式（如接入微信推送）需要修改 `ReviewService`。 | 1. 为每个 Service 抽取接口（`IAuthService`、`IRequestService` 等），Controller 通过 Spring DI 注入接口而非实现类。<br>2. `OrderService` 改为调用 `RequestService.lockRequest(id)` 而非直接操作 `RequestMapper`，保证需求状态变更走统一入口。<br>3. 引入 Spring 事件机制或 `INotificationSender` 接口解耦 `ReviewService` 与 `NotificationService`：评价完成后发布 `ReviewCreatedEvent`，由 `NotificationService` 监听并发送通知。

