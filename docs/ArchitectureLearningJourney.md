# 架构学习之旅

本文记录 nowtest 当前的架构状态。项目仍采用现代 Android 分层架构，但原始 Now in Android 的 Topic、NewsResource、For You 信息流、Saved 收藏和 Interests 兴趣选择业务链路已经下线。

## 当前分层

- UI 层：Jetpack Compose、Material 3、Navigation Compose。For You、Saved、Interests 等页面按产品要求保留空白页面壳和导航入口。
- 状态管理：ViewModel、Kotlin Flow、单向数据流。当前主要活跃状态来自搜索页面和设置页面。
- 数据层：保留 Repository 模式、DataStore、Room 和 Network 模块。已删除的业务模型、表、DAO、网络 DTO 和同步分支不再保留。
- 依赖注入：继续使用 Hilt/KSP。DI 模块保留，但只绑定仍在使用的仓库和基础设施对象。
- 同步：同步模块已删除，应用启动不再调度 WorkManager 同步任务。

## 数据层边界

当前仍保留的数据能力：

- `UserDataRepository`：用于设置页读取和写入主题品牌、深色模式、动态取色偏好。
- `NtPreferencesDataSource`：基于 Proto DataStore 保存 `theme_brand`、`dark_theme_config`、`use_dynamic_color`。
- `NtDatabase`：Room 数据库组件本身。由于 Room 要求至少一个 Entity，保留 `database_metadata` 作为基础设施 metadata 表。
- `NtNetworkDataSource`：网络数据源抽象本身。当前不再暴露 Topic/NewsResource 业务接口。

已删除的数据能力：

- Topic 链路：`Topic`、`TopicEntity`、`TopicDao`、`TopicsRepository`、`NetworkTopic`、topic JSON、topic changelist。
- NewsResource 链路：`NewsResource`、`NewsResourceEntity`、`NewsResourceDao`、`NewsRepository`、`NetworkNewsResource`、news JSON、news changelist。
- 用户内容状态：关注、收藏、已读、onboarding 相关字段和方法。

## UI 状态

搜索页当前使用本地测试内容作为搜索源，不依赖旧的 FTS、RecentSearch、SearchContents、Topic 或 NewsResource 数据链路。

设置页通过 `UserDataRepository.userData` 订阅 DataStore 中的设置项，并调用仓库方法写入设置变化。

## 判断原则

基础设施组件本身应保留；组件内部只服务已下线功能的字段、表、模型、接口、同步分支和测试数据应删除。具体边界以仓库根目录的 `INFRASTRUCTURE_COMPONENTS.md` 为准。
