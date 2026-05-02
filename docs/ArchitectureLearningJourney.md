# 架构学习之旅

本文记录 nowtest 当前的架构状态。项目采用现代 Android 分层架构，当前重点维护应用壳、顶层导航、搜索、设置、设计系统和数据基础设施。

## 当前分层

- UI 层：Jetpack Compose、Material 3、Navigation 3。“测试一”“测试二”“测试三”保留为顶层导航页面壳，Search 和 Settings 承载当前主要交互。
- 状态管理：ViewModel、Kotlin Flow、单向数据流。搜索页面和设置页面通过状态流驱动界面。
- 数据层：Repository 模式、DataStore、Room 和 Network 模块共同组成数据基础设施。
- 依赖注入：使用 Hilt/KSP 绑定仓库、数据源和基础设施对象。
- 测试：通过测试替身、Hilt 测试 API、Turbine、Truth、Compose Test 和 Roborazzi 验证行为与界面。

## 数据层边界

当前数据层能力：

- `UserDataRepository`：用于设置页读取和写入深色模式偏好。
- `NtPreferencesDataSource`：基于 Proto DataStore 保存 `dark_theme_config`。
- `NtDatabase`：Room 数据库组件，当前使用 `database_metadata` 作为基础设施 metadata 表。
- `NtNetworkDataSource`：网络数据源抽象，demo/prod 可按当前产品需求提供不同实现。

## UI 状态

搜索页使用本地测试内容作为搜索源，`SearchViewModel` 负责保存 query、计算结果状态并记录搜索事件。

设置页通过 `UserDataRepository.userData` 订阅 DataStore 中的设置项，并调用仓库方法写入设置变化。

顶层页面壳通过各自 `route:*:contract` 模块提供 route key，通过 `route:*:scene` 模块提供 Navigation entry，由 `app` 模块统一装配。

## 维护原则

基础设施组件本身应保留，例如 DataStore、Room、Repository、Network、DI、Hilt、KSP、导航、设计系统和测试底座。组件内部的字段、表、接口、状态或测试数据是否保留，应以当前功能是否实际使用为准。具体边界以仓库根目录的 `INFRASTRUCTURE_COMPONENTS.md` 为准。