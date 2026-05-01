# 项目基础设施组件边界说明

本文档用于说明 nowtest 项目中哪些内容属于基础设施组件，删除业务功能时不能直接删除；同时说明哪些只是基础设施组件内部承载的业务字段、方法、资源或测试数据，功能下线后可以清理。

核心规则：

- 基础设施是可被多个功能复用的机制、模块、接口、运行时能力或工程能力。
- 业务字段不是基础设施。即使字段位于 DataStore、数据库、仓库、DI 模块或网络 DTO 内，只要它只服务已经删除的功能，就可以清理。
- 删除功能时优先保留底座，删除业务入口、UI、状态、字段、仓库方法、测试数据和专用依赖绑定。
- 如果产品要求保留页面，只保留页面壳、导航键和必要路由；页面内部业务逻辑、按钮、列表、状态流、ViewModel、用例调用等都不应保留。

## 基础设施总览

| 组件 | 主要路径 | 为什么是基础设施 | 删除功能时的规则 |
| --- | --- | --- | --- |
| 应用壳与单 Activity 入口 | `app/src/main/kotlin/com/lhzkml/nowtest/MainActivity.kt`, `NtApplication.kt`, `ui/NtApp.kt`, `ui/NtAppState.kt` | 承载应用启动、主题、导航宿主、全局状态、Hilt 应用入口 | 不因删除某个功能而删除。只能移除该功能在应用壳中的业务入口或依赖 |
| Gradle 模块结构 | `settings.gradle.kts`, `build.gradle.kts`, `gradle/`, `build-logic/` | 定义模块、插件、风味、构建类型、依赖版本和工程约束 | 不删除构建体系。可删除已下线功能模块的依赖声明，前提是页面壳也不再需要该模块 |
| Hilt / DI / KSP | `core/*/di/`, `app/di/`, `sync/work/*/di/`, `build-logic/convention/HiltConventionPlugin.kt` | 提供依赖注入、代码生成和对象图装配 | 保留 Hilt、KSP、通用 Module。只删除只服务下线功能的绑定、Provider 或构造参数 |
| 协程调度器与应用作用域 | `core/common/src/main/kotlin/.../di/DispatchersModule.kt`, `CoroutineScopesModule.kt`, `NtDispatchers.kt` | 全局异步执行能力，仓库、同步、数据层都会使用 | 不因业务功能删除而删除。只清理不再被调用的业务协程逻辑 |
| DataStore 组件 | `core/datastore/`, `core/datastore-proto/`, `core/datastore-test/` | 保存用户偏好和轻量用户状态，是数据持久化机制 | 保留 DataStore、Serializer、DI、测试替身。可删除已下线功能的 proto 字段、读写方法和迁移残留 |
| Room 数据库 | `core/database/` | 本地结构化数据、DAO、实体、FTS、迁移 | 保留数据库基础能力。可删除只属于下线功能的表、列、DAO 查询和迁移逻辑，但要保证 schema/migration 正确 |
| 数据仓库层 | `core/data/src/main/kotlin/.../repository/`, `core/data/di/` | 屏蔽本地、网络、DataStore 细节，为 UI/domain 提供数据流接口 | 保留仓库模式和仍被其他功能使用的仓库。可删除下线功能专用接口方法、状态字段、组合逻辑和测试数据 |
| 数据模型 | `core/model/`, `core/data/model/`, `core/database/model/`, `core/network/model/` | 跨层数据契约和映射对象 | 保留仍被其他功能使用的模型。可删除下线功能专用字段，例如收藏、关注、兴趣选择状态 |
| Domain / UseCase | `core/domain/` | 复用业务查询和组合逻辑，供多个 feature 使用 | 保留仍被多个页面或核心流程使用的用例。只服务下线功能的用例可以删除 |
| 网络层 | `core/network/` | Retrofit、OkHttp、demo/prod 数据源、网络 DTO | 保留网络栈和数据源抽象。可删除接口返回中只给下线功能用的字段或映射 |
| 同步与 WorkManager | `sync/work/`, `sync/sync-test/`, `core/data/util/SyncManager.kt` | 后台同步、变更列表、通知订阅、测试替身 | 保留同步框架。可删除同步过程中只同步下线功能数据的分支 |
| 通知组件 | `core/notifications/`, `sync/work/src/prod/.../SyncNotificationsService.kt` | 系统通知、demo/prod 通知实现、同步通知入口 | 保留通知基础设施。可删除某个下线功能专属通知类型 |
| 导航基础设施 | `core/navigation/`, `feature/*/api/.../*NavKey.kt`, `app/navigation/TopLevelNavItem.kt` | 类型安全导航状态、Navigator、页面路由键 | 保留 `core:navigation`。具体页面 NavKey/TopLevelNavItem 是产品页面入口，不是底层基础设施；是否保留取决于页面是否还要存在 |
| 设计系统 | `core/designsystem/` | 主题、颜色、字体、按钮、导航栏、图标、通用组件 | 不因删除某个功能而删除。可删除只服务下线功能且没有复用价值的图标、预览或专用组件 |
| 共享 UI | `core/ui/` | 新闻卡片、信息流、预览参数、通用 UI 行为 | 保留被多个功能复用的 UI。可删除 Saved/Interests 专属按钮、状态参数或预览数据 |
| 分析埋点 | `core/analytics/`, `core/ui/AnalyticsExtensions.kt`, `sync/work/.../AnalyticsExtensions.kt` | 统一埋点接口、demo/prod 实现和 Compose Local | 保留埋点框架。可删除下线功能的 screen/event 名称和事件参数 |
| 测试基础设施 | `core/testing/`, `core/data-test/`, `core/datastore-test/`, `core/screenshot-testing/`, `ui-test-hilt-manifest/`, `sync/sync-test/` | 测试 Runner、Fake 仓库、测试 DI、截图工具、同步测试替身 | 保留测试底座。可删除下线功能的测试类、测试数据字段和 Fake 方法 |
| 性能与质量工具 | `benchmarks/`, `lint/`, `spotless/`, `.github/workflows/`, `kokoro/` | 基准测试、lint 规则、格式化和 CI | 不因功能删除而删除。可删除只覆盖下线功能路径的 benchmark/action |
| Catalog / 组件预览应用 | `app-nt-catalog/` | 展示设计系统组件和 UI 目录 | 保留作为开发辅助。可清理下线功能专属展示项 |

## 组件内可以删除的内容

这些内容不因为位于基础设施模块中就自动变成基础设施：

- DataStore proto 的 active 字段、读写函数、迁移字段，只要它们只服务已删除功能。`reserved` 编号和名称是兼容性记录，不代表功能仍存在，通常应保留避免字段编号被误复用。
- Repository 接口方法、实现方法、Flow 状态、组合仓库逻辑，只要它们只服务已删除功能。
- Room 表、列、DAO 查询、Entity 字段、FTS 字段，只要它们只服务已删除功能。
- Network DTO 字段、映射字段、demo JSON 字段，只要它们只服务已删除功能。
- DI 绑定、Provider、构造函数参数，只要它们只为已删除功能装配依赖。
- Model 字段、UI state 字段、PreviewParameterProvider 数据，只要它们只服务已删除功能。
- Analytics event、screenName、参数，只要它们只记录已删除功能。
- 测试类、Fake 方法、测试数据字段，只要它们只覆盖已删除功能。
- 设计系统里只给已删除功能使用、没有通用语义的图标、按钮封装或预览。

## 已删除功能相关边界

### Interests / 兴趣选择

应保留：

- `core:datastore` 组件本身、DataStore DI、Serializer。
- `core:data` 仓库层本身。
- `core:domain` 中仍被搜索、主题页或信息流使用的用例。
- `core:navigation` 导航机制。
- 如果产品仍要求页面存在，则保留 `InterestsNavKey`、页面 entry 和空白 `InterestsScreen`。

可删除：

- 关注主题、兴趣选择、onboarding 相关 active 字段，例如旧的 `followed_topic_ids`、`should_hide_onboarding`、followable UI state。若它们只出现在 proto `reserved` 中，表示字段已经被移除并保留兼容记录，不应按功能残留处理。
- `setTopicIdFollowed`、`setFollowedTopicIds`、兴趣选择列表、主题关注按钮、onboarding CTA 等业务方法和 UI。
- 只覆盖兴趣选择行为的测试、Fake 方法、截图或 benchmark。
- 只给兴趣选择页使用的 DI 依赖和 ViewModel。

注意：`Topic` 内容本身不是兴趣选择功能。主题页、搜索、新闻卡片 topic 标签仍会使用 Topic 数据时，不应删除 `TopicsRepository`、`TopicDao`、`NetworkTopic` 或 `TopicEntity`。

### Saved / Bookmarks / 标签收藏

应保留：

- `core:data` 仓库模式、`UserDataRepository`、`UserNewsResourceRepository` 这类仍被新闻阅读状态使用的接口。
- `core:ui` 新闻卡片和信息流组件中仍被 ForYou、Topic、Search 使用的部分。
- 如果产品仍要求页面存在，则保留 `BookmarksNavKey`、页面 entry 和空白 `BookmarksScreen`。

可删除：

- 收藏新闻 active 字段，例如旧的 `bookmarked_news_resource_ids`、`isSaved`、`isBookmarked`。若只剩 proto `reserved` 名称，不代表收藏功能仍存在。
- 收藏/取消收藏方法，例如旧的 `setNewsResourceBookmarked`、`observeAllBookmarked`。
- Saved 页面列表、空状态、收藏按钮、收藏过滤器、收藏相关 snackbar。
- 只给收藏功能服务的测试、Fake 方法、Preview 数据和埋点。

注意：`viewed_news_resource_ids` 属于已读状态，不等同于收藏；如果 ForYou、Topic 或其他页面仍使用已读能力，不应删除。

## 当前仍然需要保留的核心数据能力

这些能力在删除 Interests 和 Saved 后仍然有合理用途：

- 新闻资源同步和展示：`NewsRepository`、`OfflineFirstNewsRepository`、`NewsResourceDao`、`NetworkNewsResource`、`UserNewsResourceRepository`。
- 主题数据展示和搜索：`TopicsRepository`、`OfflineFirstTopicsRepository`、`TopicDao`、`NetworkTopic`、`TopicEntity`。
- 搜索：`SearchContentsRepository`、`RecentSearchRepository`、`GetSearchContentsUseCase`、`GetRecentSearchQueriesUseCase`。
- 已读状态：`UserData.viewedNewsResources`、DataStore `viewed_news_resource_ids`、`setNewsResourceViewed`。
- 设置：`theme_brand`、`dark_theme_config`、`use_dynamic_color` 及对应 Repository 方法。
- 同步版本：`topicChangeListVersion`、`newsResourceChangeListVersion`，用于增量同步。

## 删除功能时的检查清单

1. 先判断目标是功能、页面、字段还是基础设施组件。
2. 如果是基础设施组件，默认不删除；只清理其中与下线功能强绑定的字段、方法和绑定。
3. 搜索功能名和同义词，例如 `interest`、`follow`、`onboarding`、`bookmark`、`saved`、`isSaved`。
4. 清理 UI：页面内部组件、按钮、状态、ViewModel、Preview、字符串和资源。
5. 清理数据链路：Model 字段、Repository API、DataStore proto 字段、Room 字段、Network DTO 字段、映射逻辑。
6. 清理 DI：删除不再使用的 Provider、Binds、构造参数和测试替换模块条目。
7. 清理测试：删除或改写只验证下线功能的测试、Fake 字段、测试数据。
8. 保留页面时，只保留能让空白页面正常进入和渲染的最小导航与页面壳。
9. 运行构建或相关测试，确认没有未使用引用、KSP/Hilt 生成错误、Room schema 错误或 proto 生成错误。

## 判断口诀

- 能被多个功能继续复用的是基础设施。
- 只描述某个功能状态的是业务字段。
- 只为某个页面装配的是页面依赖。
- 只让工程能编译、注入、持久化、联网、同步、测试的是底座。
