# 模块化学习之旅

nowtest 仍保持模块化结构：`app/` 负责应用壳和导航整合，`feature/` 放页面模块，`core/` 放可复用能力。

## 模块类型

- `app`：应用入口、单 Activity、全局导航、主题和 Hilt 应用入口。
- `feature:*:api`：页面导航键和最小 API。
- `feature:*:impl`：页面实现。已下线功能如果仍要保留页面，只保留空白页面壳。
- `core:data`：仓库模式、网络监控、时区监控和数据层 DI。只保留仍使用的 `UserDataRepository`。
- `core:datastore` / `core:datastore-proto`：用户设置持久化。
- `core:database`：Room 数据库组件。当前仅保留基础设施 metadata 表。
- `core:network`：网络模块和 demo/prod 数据源抽象。旧业务 DTO 和静态 JSON 已删除。
- `core:model`：共享外部模型。当前保留设置相关模型。
- `core:designsystem` / `core:ui`：设计系统、通用 UI 和埋点辅助。
- `core:testing` / `core:data-test` / `core:datastore-test`：测试基础设施。

## 当前已移除的业务模块内容

Topic、NewsResource、兴趣选择、收藏、关注、已读新闻、旧最近搜索、旧 FTS/SearchContents/RecentSearch 等链路不再作为数据层能力保留。对应模型、仓库、DAO、网络 DTO、静态数据、测试替身和同步分支都应删除。

## 保留空页面的规则

For You、Saved、Interests 这类仍被产品要求保留入口的页面，只保留：

- `NavKey`
- navigation entry
- 空白 `Screen`
- 必要字符串资源

不保留 ViewModel、UseCase、Repository 调用、列表、按钮、卡片、状态流、预览数据或测试数据。

## 维护原则

删除功能时不要删除基础设施模块本身；要删除的是这些模块内部与已下线业务强绑定的字段、方法、表、模型、绑定和测试数据。具体边界以 `INFRASTRUCTURE_COMPONENTS.md` 为准。
