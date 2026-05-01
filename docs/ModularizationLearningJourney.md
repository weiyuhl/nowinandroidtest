# 模块化学习之旅

nowtest 保持多模块结构：`app/` 负责应用壳和导航整合，`feature/` 放页面模块，`core/` 放可复用能力。

## 模块类型

- `app`：应用入口、单 Activity、全局导航、主题和 Hilt 应用入口。
- `feature:*:api`：页面导航键和最小 API。
- `feature:*:impl`：页面实现、页面级状态管理和 navigation entry。
- `core:data`：仓库模式、网络监控、时区监控和数据层 DI。
- `core:datastore` / `core:datastore-proto`：用户设置持久化。
- `core:database`：Room 数据库组件。
- `core:network`：网络模块和 demo/prod 数据源抽象。
- `core:model`：共享外部模型。
- `core:designsystem` / `core:ui`：设计系统、通用 UI 和埋点辅助。
- `core:testing` / `core:data-test` / `core:datastore-test`：测试基础设施。

## 页面模块

For You、Bookmarks、Interests 当前作为顶层导航页面壳存在。它们保留：

- `NavKey`
- navigation entry
- `Screen`
- 必要字符串资源

Search 和 Settings 是当前主要交互模块。Search 管理本地测试内容搜索，Settings 管理用户主题偏好。

## 维护原则

不要因为某个功能变化而删除基础设施模块本身。需要判断的是组件内部的字段、表、模型、接口、绑定和测试数据是否仍被当前功能使用。具体边界以 `INFRASTRUCTURE_COMPONENTS.md` 为准。
