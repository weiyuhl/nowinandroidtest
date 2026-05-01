# 项目结构、组件与架构分析

本文档记录当前工作区 `nowinandroid-test` 的项目结构、模块职责、技术组件与整体架构分析。

## 1. 项目整体定位

这是一个多模块 Android 项目，项目名为 `nowtest`，使用 Kotlin、Jetpack Compose、Hilt、Room、Proto DataStore、WorkManager、Navigation 3 等 Android 现代技术栈。

项目整体风格接近 Android 官方推荐架构：单 Activity、Compose UI、ViewModel 暴露状态、Repository 管理数据、Hilt 负责依赖注入、WorkManager 负责后台同步。

当前工作区看起来处在功能裁剪或本地化改造阶段：大量外部服务、远程同步、prod 相关文件已被删除或修改；数据层和同步逻辑明显比完整 Now in Android 风格项目更轻量。

## 2. 顶层目录结构

```text
nowinandroid-test/
├── .github/                     # GitHub issue/PR 模板、Renovate、CI workflow
├── app/                         # 主应用模块
├── app-nt-catalog/              # 组件/设计系统 catalog 应用模块
├── benchmarks/                  # 宏基准测试、Baseline Profile
├── build-logic/                 # 自定义 Gradle convention plugins
├── core/                        # 核心共享模块
├── docs/                        # 架构与模块化学习文档
├── feature/                     # 功能模块
├── gradle/                      # Gradle wrapper 与版本目录
├── kokoro/                      # Kokoro/CI 构建脚本
├── lint/                        # 自定义 lint 规则模块
├── sync/                        # 后台同步相关模块
├── ui-test-hilt-manifest/       # UI 测试 Hilt manifest 辅助模块
├── AGENTS.md                    # Agent/自动化助手相关说明
├── build.gradle.kts             # 根构建脚本
├── settings.gradle.kts          # 模块声明与仓库配置
├── gradle.properties            # Gradle 全局属性
├── gradlew / gradlew.bat        # Gradle wrapper 启动脚本
├── build_android_release.sh     # Android release 构建脚本
├── CLAUDE.md                    # 项目给 Claude Code 的开发说明
├── CODEOWNERS                   # 代码所有者配置
├── CODE_OF_CONDUCT.md           # 社区行为准则
├── CONTRIBUTING.md              # 贡献指南
├── INFRASTRUCTURE_COMPONENTS.md # 基础设施组件说明
└── README.md                    # 项目说明
```

主要模块声明集中在 `settings.gradle.kts`。当前启用了 `TYPESAFE_PROJECT_ACCESSORS`，因此 Gradle 依赖中可以使用 `projects.core.data`、`projects.feature.foryou.impl` 这类类型安全访问器。

## 3. Gradle 模块清单

### 3.1 应用与测试模块

```text
:app
:app-nt-catalog
:benchmarks
:ui-test-hilt-manifest
```

- `:app` 是主应用模块，依赖所有 feature impl 与必要 core 模块。
- `:app-nt-catalog` 用于展示或测试设计系统/组件 catalog。
- `:benchmarks` 负责 Macrobenchmark 与 Baseline Profile。
- `:ui-test-hilt-manifest` 用于 UI 测试中补充 Hilt manifest。

### 3.2 core 模块

```text
:core:analytics
:core:common
:core:data
:core:data-test
:core:database
:core:datastore
:core:datastore-proto
:core:datastore-test
:core:designsystem
:core:domain
:core:model
:core:navigation
:core:network
:core:notifications
:core:screenshot-testing
:core:testing
:core:ui
```

职责说明：

| 模块 | 职责 |
| --- | --- |
| `core:analytics` | 分析事件抽象、Compose local analytics helper、按 flavor 绑定实现 |
| `core:common` | 通用协程 dispatcher、application scope 等基础设施 |
| `core:data` | Repository、网络状态、时区状态、数据协调 |
| `core:data-test` | data 层测试替身 |
| `core:database` | Room 数据库与实体 |
| `core:datastore` | Proto DataStore 偏好设置读写 |
| `core:datastore-proto` | DataStore 使用的 protobuf 定义与生成代码 |
| `core:datastore-test` | DataStore 测试替身 |
| `core:designsystem` | Compose 设计系统组件、主题、图标、截图基线 |
| `core:domain` | 领域层用例位置，目前功能较轻 |
| `core:model` | 纯数据模型，适合作为 JVM library |
| `core:navigation` | Navigation 3 状态管理与 Navigator 封装 |
| `core:network` | 网络数据源、Retrofit/demo/prod flavor 绑定 |
| `core:notifications` | 通知相关抽象和按 flavor 实现 |
| `core:screenshot-testing` | Roborazzi 截图测试基础设施 |
| `core:testing` | 通用测试工具、Hilt test runner、测试规则 |
| `core:ui` | 通用 UI helper、CompositionLocal、jank tracking 等 |

### 3.3 feature 模块

```text
:feature:foryou:api
:feature:foryou:impl
:feature:interests:api
:feature:interests:impl
:feature:bookmarks:api
:feature:bookmarks:impl
:feature:search:api
:feature:search:impl
:feature:settings:impl
```

功能模块采用 `api` / `impl` 拆分：

- `api` 模块通常只暴露导航 key、轻量资源或跨模块入口。
- `impl` 模块包含 Compose 页面、ViewModel、导航 entry provider。
- `settings` 目前只有 `impl`，作为顶层设置对话框使用。

当前 feature 包括：

| 功能 | api | impl | 说明 |
| --- | --- | --- | --- |
| For You | 有 | 有 | 首页/推荐页 |
| Bookmarks | 有 | 有 | 收藏页 |
| Interests | 有 | 有 | 兴趣页 |
| Search | 有 | 有 | 搜索页，目前使用本地测试内容 |
| Settings | 无 | 有 | 设置弹窗，读写用户主题偏好 |

### 3.4 sync 模块

```text
:sync:work
:sync:sync-test
```

- `sync:work`：WorkManager 后台同步实现。
- `sync:sync-test`：同步相关测试替身，例如永不同步的 `SyncManager`。

当前同步 worker 已被简化，主要记录 sync started / sync finished 事件，没有实际远程数据拉取。

### 3.5 build-logic 模块

`build-logic/convention` 定义项目自有 Gradle convention plugins，包括：

```text
nowtest.android.application
nowtest.android.application.compose
nowtest.android.application.flavors
nowtest.android.application.jacoco
nowtest.android.feature.api
nowtest.android.feature.impl
nowtest.android.library
nowtest.android.library.compose
nowtest.android.library.jacoco
nowtest.android.lint
nowtest.android.room
nowtest.android.test
nowtest.hilt
nowtest.jvm.library
nowtest.root
```

这些插件统一配置 Android、Kotlin、Compose、Hilt、Room、Jacoco、Spotless、Lint、flavor、测试设备等构建逻辑。

## 4. 构建变体

项目定义两种 product flavor：

```text
demo
prod
```

项目定义两种 build type：

```text
debug
release
```

组合后常见变体包括：

```text
demoDebug
demoRelease
prodDebug
prodRelease
```

根据仓库说明，开发应始终使用 `demoDebug`。`prod` 需要未公开后端服务，当前工作区还删除了不少 prod 相关文件，因此 prod 变体需要额外谨慎。

主应用信息：

- applicationId：`com.lhzkml.nowtest`
- versionCode：`8`
- versionName：`0.1.2`
- release 默认启用 R8/minify
- release 使用 debug signing 方便本地构建
- release 可自动生成 Baseline Profile
- dependency guard 监控 `prodReleaseRuntimeClasspath`

## 5. 主要技术组件

### 5.1 语言与构建

- Kotlin `2.3.0`
- Android Gradle Plugin `9.0.0`
- KSP `2.3.4`
- Gradle Kotlin DSL
- Version Catalog：`gradle/libs.versions.toml`
- 自定义 convention plugins
- Spotless `8.3.0`
- ktlint `1.4.0`
- Jacoco `0.8.12`
- Dependency Guard
- Protobuf Gradle Plugin

### 5.2 UI 技术栈

- Jetpack Compose BOM `2025.09.01`
- Compose Foundation
- Compose Material 3
- Material 3 Adaptive
- Material 3 Adaptive Navigation Suite
- Navigation 3 runtime/ui
- Compose Runtime Tracing
- Compose UI Test
- Coil / Coil Compose / Coil SVG
- AndroidX Activity Compose
- AndroidX Window
- AndroidX Core Splashscreen

### 5.3 架构组件

- Lifecycle ViewModel
- Lifecycle Runtime Compose
- SavedStateHandle
- Kotlin Flow / StateFlow
- Hilt ViewModel
- Hilt Android
- Hilt WorkManager
- WorkManager
- Room
- Proto DataStore

### 5.4 网络与序列化

- Retrofit
- OkHttp Logging Interceptor
- kotlinx.serialization JSON
- flavor-specific network module
- demo 数据源与 prod Retrofit 数据源分离

当前 `RetrofitNtNetwork` 类存在但实现很轻，说明网络层基础设施还在，但业务数据接口已明显裁剪。

### 5.5 测试技术栈

- JUnit 4
- Kotlin Test
- Truth
- Turbine
- Robolectric
- Roborazzi
- AndroidX Test Core/Runner/Rules
- Espresso
- UI Automator
- WorkManager Testing
- Compose UI Test
- Hilt Android Testing

项目约定不使用 mock 框架，而是用 Hilt 测试 API 注入测试替身。

### 5.6 性能、发布与诊断

- Baseline Profile
- Macrobenchmark
- Profile Installer
- JankStats / metrics-performance
- AndroidX Tracing
- StrictMode debug 检测

## 6. 应用架构

整体数据和事件流向：

```text
Compose UI
   ↓ 用户事件
ViewModel
   ↓ 调用接口 / 暴露 StateFlow
Repository
   ↓ 协调数据源
DataStore / Room / Network / WorkManager
```

这是典型单向数据流 UDF：

- UI 观察状态，不直接持有业务数据源。
- ViewModel 将 repository 的 Flow 转换为 UI state。
- UI 事件回调到 ViewModel。
- Repository 隐藏本地/远程/持久化细节。
- Hilt 负责为 UI、ViewModel、Worker、Repository 注入依赖。

## 7. 应用入口与 Compose Root

### 7.1 Application

`NtApplication`：

- 标注 `@HiltAndroidApp`
- 实现 `ImageLoaderFactory`
- 注入 Coil `ImageLoader`
- debug 模式设置 StrictMode
- 启动时初始化 WorkManager 同步
- 启动 profile verifier logger

### 7.2 MainActivity

`MainActivity`：

- 标注 `@AndroidEntryPoint`
- 安装 SplashScreen
- 注入 `JankStats`、`NetworkMonitor`、`TimeZoneMonitor`、`AnalyticsHelper`
- 使用 `MainActivityViewModel` 获取主题相关 UI state
- 监听系统深色主题与用户设置组合结果
- 调用 `enableEdgeToEdge`
- `setContent` 中提供 `LocalAnalyticsHelper`、`LocalTimeZone`
- 套用 `NtTheme`
- 挂载根 Composable `NtApp`

### 7.3 MainActivityViewModel

`MainActivityViewModel` 从 `UserDataRepository.userData` 读取用户偏好，并转换为：

- splash 是否保持显示
- 是否使用 Android theme
- 是否禁用动态颜色
- 是否使用深色主题

状态通过 `StateFlow<MainActivityUiState>` 暴露给 Activity。

## 8. AppState 与全局 UI 状态

`NtAppState` 聚合 app 级状态：

- `NavigationState`
- `isOffline`
- `currentTimeZone`

`isOffline` 由 `NetworkMonitor.isOnline` 反转得到。`currentTimeZone` 来自 `TimeZoneMonitor.currentTimeZone`。

`rememberNtAppState` 在 Compose 中创建并 remember 该状态对象，同时触发导航 jank tracking side effect。

## 9. 导航架构

项目使用 Navigation 3，而不是传统字符串 route 的 Navigation Compose graph。

核心概念：

- 每个页面以 typed `NavKey` 表示。
- 顶层页面有独立 back stack。
- `NavigationState` 保存顶层 stack 与每个顶层页面的 sub stack。
- `Navigator` 封装 navigate / back 行为。
- feature impl 通过 `EntryProviderScope<NavKey>` 注册 entry。
- app root 通过 `NavDisplay` 渲染当前 entries。

顶层导航项：

```text
ForYouNavKey
BookmarksNavKey
InterestsNavKey
```

搜索页：

```text
SearchNavKey
```

`NtApp` 中注册：

```kotlin
entryProvider {
    forYouEntry()
    bookmarksEntry()
    interestsEntry()
    searchEntry(navigator)
}
```

这使 app 模块负责组装导航，feature 模块负责贡献自己的页面 entry。

## 10. UI 层

UI 使用 Jetpack Compose 和 Material 3。

主要结构：

- `NtTheme`：应用主题
- `NtBackground` / `NtGradientBackground`：背景容器
- `NtNavigationSuiteScaffold`：自适应导航栏/导航轨/导航抽屉
- `NtTopAppBar`：顶栏
- `NavDisplay`：导航内容区域
- `SettingsDialog`：设置弹窗
- Snackbar：离线提示

UI 设计系统集中在 `core:designsystem`，业务页面位于 feature impl 模块。

## 11. 数据层

### 11.1 Repository

当前主要 repository 是 `UserDataRepository`，实现为 `OfflineFirstUserDataRepository`。

它负责：

- 暴露用户偏好 `Flow<UserData>`
- 更新主题品牌
- 更新深色主题配置
- 更新动态颜色偏好
- 记录相关 analytics 事件

### 11.2 DataStore

`NtPreferencesDataSource` 使用 Proto DataStore 保存用户偏好：

- theme brand
- dark theme config
- dynamic color preference

DataStore proto 定义位于 `core:datastore-proto`。

### 11.3 Room

`core:database` 保留 Room 基础设施。当前 `NtDatabase` 中只看到 `DatabaseMetadataEntity`，version 为 `1`，并启用了 schema export。

这说明历史上可能存在更完整内容数据库，但当前工作区已经大幅裁剪数据实体。

### 11.4 Network

`core:network` 提供网络数据源抽象：

- `NtNetworkDataSource`
- demo flavor 绑定 demo network data source
- prod flavor 绑定 Retrofit network data source
- Retrofit/OkHttp/serialization 基础设施仍存在

当前 Retrofit 实现很轻，业务接口可能已被移除或尚未恢复。

## 12. 同步架构

同步由 `NtApplication.onCreate()` 调用 `Sync.initialize(context)` 开始。

`Sync.initialize` 使用 WorkManager：

- 调用 `WorkManager.getInstance(context)`
- 使用 `enqueueUniqueWork`
- work name 为 `SyncWorkName`
- policy 为 `ExistingWorkPolicy.KEEP`
- work request 来自 `SyncWorker.startUpSyncWork()`

`SyncWorker`：

- 标注 `@HiltWorker`
- 继承 `CoroutineWorker`
- 使用 IO dispatcher
- 注入 `AnalyticsHelper`
- 支持 foreground info
- 当前 `doWork()` 只记录同步开始/结束，并返回 success

当前同步模块更像保留框架，而不是完整业务同步实现。

## 13. 依赖注入架构

项目使用 Hilt：

- `@HiltAndroidApp`：Application
- `@AndroidEntryPoint`：Activity
- `@HiltViewModel`：ViewModel
- `@HiltWorker`：WorkManager worker
- `@Module` + `@InstallIn`：依赖绑定

典型绑定：

```text
UserDataRepository -> OfflineFirstUserDataRepository
NetworkMonitor -> ConnectivityManagerNetworkMonitor
TimeZoneMonitor -> TimeZoneBroadcastMonitor
NtNetworkDataSource -> DemoNtNetworkDataSource / RetrofitNtNetwork
SyncManager -> WorkManagerSyncManager 或测试实现
AnalyticsHelper -> demo/prod flavor 实现
Notifications -> demo/prod flavor 实现
```

测试模块通过 Hilt 替换 production 绑定，避免使用 mock 框架。

## 14. 功能模块现状

### 14.1 For You

- `feature:foryou:api` 提供 `ForYouNavKey`
- `feature:foryou:impl` 提供 `ForYouScreen` 与 `forYouEntry`
- 是默认启动 tab

### 14.2 Bookmarks

- `feature:bookmarks:api` 提供 `BookmarksNavKey`
- `feature:bookmarks:impl` 提供 `BookmarksScreen` 与 `bookmarksEntry`

### 14.3 Interests

- `feature:interests:api` 提供 `InterestsNavKey`
- `feature:interests:impl` 提供 `InterestsScreen` 与 `interestsEntry`

### 14.4 Search

- `feature:search:api` 提供 `SearchNavKey`
- `feature:search:impl` 提供 `SearchScreen`、`SearchViewModel`、本地测试搜索内容
- `SearchViewModel` 使用 `SavedStateHandle` 保存 query
- query 长度小于 2 时显示 empty query
- query 足够长时搜索本地测试内容
- 搜索触发时记录 analytics 事件

### 14.5 Settings

- `feature:settings:impl` 提供 `SettingsDialog` 与 `SettingsViewModel`
- 没有独立 api 模块
- 通过 `UserDataRepository` 读取和写入用户设置
- 控制 theme brand、dark theme config、dynamic color preference

## 15. 测试架构

项目测试分层较完整：

```text
src/test/              # 普通 JVM 单元测试
src/testDemo/          # demo flavor 本地测试
src/androidTest/       # 插桩测试
src/androidTestDemo/   # demo flavor 插桩测试
```

测试工具模块：

- `core:testing`
- `core:data-test`
- `core:datastore-test`
- `sync:sync-test`
- `core:screenshot-testing`
- `ui-test-hilt-manifest`

测试策略：

- 不使用 mock 框架
- 使用测试替身实现真实接口
- 使用 Hilt 注入替换依赖
- Flow 使用 Turbine 测试
- UI 使用 ComposeTestRule
- 截图使用 Roborazzi
- benchmark 使用 Macrobenchmark + managed device

## 16. 现有文档与辅助说明

仓库除根 `README.md` 外，还有若干补充文档：

- `docs/ArchitectureLearningJourney.md`：架构学习说明。
- `docs/ModularizationLearningJourney.md`：模块化学习说明。
- `INFRASTRUCTURE_COMPONENTS.md`：基础设施组件说明。
- `AGENTS.md`：自动化助手/agent 相关说明。
- 各模块目录下的 `README.md`：模块职责说明。

## 17. 构建、CI 与质量检查

推荐命令：

```bash
./gradlew assembleDemoDebug
./gradlew spotlessApply
./gradlew testDemoDebug
./gradlew connectedDemoDebugAndroidTest
./gradlew verifyRoborazziDemoDebug
./gradlew recordRoborazziDemoDebug
```

根据仓库说明，不应直接运行：

```bash
./gradlew test
./gradlew connectedAndroidTest
```

因为这会触发所有变体，可能因 prod flavor 失败。

CI 与自动化配置：

- `.github/workflows/Build.yaml`：主构建 workflow。
- `.github/workflows/NightlyBaselineProfiles.yaml`：夜间 Baseline Profile workflow。
- `.github/workflows/Release.yml`：发布 workflow。
- `.github/renovate.json`：依赖更新自动化配置。
- `.github/ci-gradle.properties`：CI 使用的 Gradle 属性。
- `kokoro/build.sh`：Kokoro 构建入口。
- `build_android_release.sh`：本地或 CI release 构建脚本。

项目还包含大量模块级 `README.md`，可作为各模块职责的补充说明。

## 18. 当前工作区变更观察

当前 git 工作区有大量未提交变更，主要集中在：

- 移除外部服务配置与实现
- 删除外部服务配置文件
- 删除远程 analytics helper
- 删除远程 sync subscriber
- 删除 sync notification service
- 删除部分 prod manifest
- 简化 `SyncWorker`
- 简化 analytics prod/demo 实现
- 修改 Gradle 依赖与 convention plugin
- 修改 release 构建脚本与 CI 脚本
- 搜索功能改用本地测试内容
- 数据层移除大量旧功能残留

这些变化说明项目正在从依赖远程/prod 服务的形态，转向更轻量的本地 demo/test 形态。

## 19. 架构优点

- 多模块边界清晰。
- `core`、`feature`、`sync`、`build-logic` 分层明确。
- feature 采用 `api` / `impl` 拆分，降低跨 feature 耦合。
- Compose UI 与 ViewModel、Repository 分离较清楚。
- typed `NavKey` 导航比字符串 route 更安全。
- Hilt 注入统一，便于替换测试依赖。
- 构建逻辑集中在 convention plugins，模块脚本较简洁。
- 测试基础设施完整，覆盖单测、UI、截图、benchmark。
- 使用 DataStore 管理用户偏好，适合小型结构化设置数据。
- WorkManager 同步基础设施仍保留，未来可恢复实际同步逻辑。

## 20. 风险与注意事项

- 当前工作区未提交变更多，可能处于迁移中间态。
- prod flavor 相关文件删除较多，prod 构建很可能需要额外验证。
- Room、Retrofit、WorkManager 等基础设施仍在，但业务使用已大幅减少，可能存在冗余依赖。
- `SyncWorker` 当前接近空实现，长期保留需要明确目的。
- `core:database` 当前只剩 metadata entity，如不再需要本地关系型数据，可评估是否移除 Room。
- `core:network` 当前 Retrofit 实现很轻，如不再需要远程后端，可评估是否移除网络层或保留接口占位。
- `feature:settings` 没有 api 模块，与其他 feature 风格不完全一致，但作为 app 顶层 dialog 当前可以接受。
- 截图测试基线通常在 Linux CI 录制，Windows 本地验证可能有差异。

## 21. 推荐阅读顺序

如果后续继续开发，建议按以下顺序理解项目：

1. `settings.gradle.kts`：确认模块列表与构建仓库配置。
2. `gradle/libs.versions.toml`：确认依赖版本与插件版本。
3. `build.gradle.kts`：确认根构建插件。
4. `build-logic/convention`：理解统一构建逻辑。
5. `app/build.gradle.kts`：理解 app 依赖和变体配置。
6. `NtApplication`：理解应用启动阶段。
7. `MainActivity`：理解 Activity、主题、CompositionLocal、root UI 装配。
8. `NtApp` / `NtAppState`：理解根 UI、导航、离线状态。
9. `core:navigation`：理解 Navigation 3 封装。
10. `feature/*/api` 与 `feature/*/impl`：理解功能模块。
11. `core:data` / `core:datastore` / `core:database` / `core:network`：理解数据层。
12. `sync:work`：理解后台同步。
13. `core:testing`、`core:data-test`、`core:datastore-test`：理解测试替身机制。

## 22. 总结

当前项目是一个经过裁剪的 Now in Android 风格多模块 Compose 应用。它保留了现代 Android 应用的主要基础设施：Compose、Material 3、Navigation 3、Hilt、Flow、DataStore、Room、WorkManager、Roborazzi、Macrobenchmark、Baseline Profile 和 convention plugins。

从当前工作区状态看，项目正在移除远程同步和旧数据功能，转向更轻量的 demo/local-only 架构。短期内应优先验证 `demoDebug` 构建和测试是否稳定；长期可以进一步清理未实际使用的 Room、Retrofit、WorkManager 或 prod flavor 基础设施。
