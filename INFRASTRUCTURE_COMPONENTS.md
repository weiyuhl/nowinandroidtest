# 基础设施组件边界说明

本文用于区分 nowtest 项目中“必须保留的基础设施组件”和“可以删除的已下线业务残留”。

核心规则：

- 基础设施是可复用的机制、模块、运行时能力或工程能力，例如 DataStore、Room 数据库组件、仓库模式、DI/Hilt/KSP、网络模块、同步模块、导航框架、设计系统、测试底座。
- 业务字段不是基础设施。即使字段位于 DataStore、Room、Repository、DI 或 Network DTO 中，只要只服务已删除功能，就可以删除。
- 删除功能时保留底座，删除业务入口、UI、状态、字段、DAO、仓库方法、网络模型、同步分支、测试数据和专用绑定。
- 如果产品仍要求页面存在，只保留 NavKey、entry 和空白页面壳；页面内部业务逻辑、按钮、列表、ViewModel、UseCase、Repository 调用都应删除。

## 需要保留的基础设施

| 组件 | 路径 | 删除功能时的规则 |
| --- | --- | --- |
| 应用壳与单 Activity | `app/src/main/kotlin/.../MainActivity.kt`, `NtApplication.kt`, `ui/NtApp.kt` | 保留应用启动、主题、导航宿主和 Hilt 入口；只删除下线功能入口或专用路由 |
| Gradle 与构建逻辑 | `settings.gradle.kts`, `build.gradle.kts`, `gradle/`, `build-logic/` | 保留构建体系；可删除已下线模块的依赖声明 |
| Hilt / DI / KSP | `core/*/di/`, `app/di/`, `sync/work/*/di/` | 保留注入体系；删除只服务下线功能的 `@Binds`、`@Provides`、构造参数 |
| DataStore | `core/datastore/`, `core/datastore-proto/`, `core/datastore-test/` | 保留 DataStore、Serializer、DI；删除已下线功能的 proto 字段和读写方法 |
| Room 数据库组件 | `core/database/` | 保留 Room 数据库组件；删除已下线功能的表、Entity、DAO、关联表、旧 schema 记录 |
| 仓库层模式 | `core/data/src/main/kotlin/.../repository/`, `core/data/di/` | 保留仍被使用的仓库和模式；删除只服务下线功能的仓库接口、实现、方法和测试替身 |
| 网络模块 | `core/network/` | 保留网络模块、demo/prod 数据源抽象、OkHttp/Json 配置；删除旧业务 DTO、接口方法、静态 JSON |
| 同步模块 | `sync/work/`, `sync/sync-test/`, `core/data/util/SyncManager.kt` | 保留 WorkManager 同步框架；删除同步旧业务数据的分支和版本字段 |
| 通知组件 | `core/notifications/` | 保留通知模块和图标资源；删除旧新闻通知负载和新闻专用通知逻辑 |
| 导航基础设施 | `core/navigation/`, `feature/*/api/.../*NavKey.kt` | 保留导航机制；具体页面入口是否保留取决于产品要求 |
| 设计系统与共享 UI | `core/designsystem/`, `core/ui/` | 保留通用组件；删除只服务下线功能的卡片、按钮、预览参数和状态 |
| 测试基础设施 | `core/testing/`, `core/data-test/`, `core/datastore-test/`, `ui-test-hilt-manifest/` | 保留测试底座；删除下线功能的测试数据、Fake 仓库和测试类 |

## 当前已下线业务残留

以下内容不属于基础设施，发现后可以删除：

- Topic 业务链路：`Topic` 模型、`TopicEntity`、`TopicDao`、`TopicsRepository`、`OfflineFirstTopicsRepository`、`NetworkTopic`、topic 静态 JSON、topic changelist、news-topic 关联表。
- NewsResource 业务链路：`NewsResource` 模型、`NewsResourceEntity`、`NewsResourceDao`、`NewsRepository`、`OfflineFirstNewsRepository`、`NetworkNewsResource`、news 静态 JSON、news changelist、新闻通知负载。
- 兴趣/关注字段：`followed_topic_ids`、`should_hide_onboarding`、`setTopicIdFollowed`、`setFollowedTopicIds`、followable UI state、onboarding CTA。
- Saved/收藏字段：`bookmarked_news_resource_ids`、`isSaved`、`isBookmarked`、`setNewsResourceBookmarked`、收藏列表、收藏按钮、收藏过滤器。
- 已读新闻字段：`viewed_news_resource_ids`、`UserData.viewedNewsResources`、`setNewsResourceViewed`。
- 只为上述功能准备的 DI 绑定、测试数据、Fake 仓库、截图测试、预览数据和 demo/prod 静态数据。

## 当前仍保留的数据能力

- 用户设置：`theme_brand`、`dark_theme_config`、`use_dynamic_color` 及对应的 `UserDataRepository` 方法。
- Room 数据库组件本身。由于 Room 要求至少一个 Entity，当前仅保留 `database_metadata` 作为基础设施 metadata 表，不承载已删除功能数据。
- 网络模块本身。当前 `NtNetworkDataSource` 仅作为基础设施抽象保留，不再暴露 Topic/NewsResource 业务接口。
- 同步模块本身。当前 `SyncWorker` 保留启动、订阅和埋点流程，不再同步 Topic/NewsResource 业务数据。

## 检查清单

1. 先判断目标是基础设施组件，还是组件内部的业务字段/表/方法。
2. 保留基础设施组件，删除其中与下线功能强绑定的字段、方法、绑定、测试数据和静态数据。
3. 搜索功能名和同义词：`Topic`、`NewsResource`、`interest`、`follow`、`bookmark`、`saved`、`viewed`、`onboarding`。
4. 检查 DataStore proto、Room schema、Repository API、Network DTO、SyncWorker 分支、DI module、Fake/test data。
5. 保留空白页面时，只保留最小导航和页面壳。
6. 运行构建或相关测试，确认没有 KSP/Hilt/Room/proto 生成错误。
