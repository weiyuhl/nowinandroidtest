# 项目结构、组件与架构分析

本文档记录 nowtest 当前源码中的项目结构、模块职责、技术组件与整体架构。文档只描述当前项目仍保留并需要维护的内容。

## 1. 项目整体定位

nowtest 是一个多模块 Android 项目，使用 Kotlin、Jetpack Compose、Hilt、Room、Proto DataStore、Navigation 3 等现代 Android 技术栈。

项目采用单 Activity、Compose UI、ViewModel 暴露状态、Repository 管理数据、Hilt 负责依赖注入的结构。当前主要维护应用壳、导航、搜索、设置、设计系统、数据基础设施、测试基础设施和构建基础设施。

## 2. 顶层目录结构

```text
./
├── .github/                     # GitHub issue/PR 模板、Renovate、CI workflow
├── .claude/                     # Claude Code 本地设置
├── app/                         # 主应用模块
├── benchmarks/                  # 宏基准测试、Baseline Profile
├── build-logic/                 # 自定义 Gradle convention plugins
├── core/                        # 核心共享模块
├── docs/                        # 架构与模块化学习文档
├── gradle/                      # Gradle wrapper 与版本目录
├── lint/                        # 自定义 lint 规则模块
├── route/                       # 页面路由模块
├── tools/                       # 代码风格、pre-push、环境初始化辅助脚本
└── ui-test-hilt-manifest/       # UI 测试 Hilt manifest 辅助模块
```

主要模块声明集中在 `settings.gradle.kts`。当前启用了 `TYPESAFE_PROJECT_ACCESSORS`，Gradle 依赖中可以使用 `projects.core.data`、`projects.route.search.scene` 这类类型安全访问器。

## 3. Gradle 模块清单

### 3.1 应用与测试模块

```text
:app
:benchmarks
:ui-test-hilt-manifest
```

- `:app` 是主应用模块，负责应用入口、主题、全局导航和 route 装配。
- `:benchmarks` 负责 Macrobenchmark 和 Baseline Profile。
- `:ui-test-hilt-manifest` 用于 UI 测试中补充 Hilt manifest。

### 3.2 core 模块

`core/` 保持独立，不依赖 `app/` 或 `route/`。它提供数据、数据库、网络、设计系统、导航、通知、测试等基础设施与共享能力。当前 `core:designsystem` 只承载 Jasmine 主题、颜色和图标，`core:ui` 承载埋点、Jank、预览、时区和返回文案等跨页面辅助能力，不作为单页面组件库使用。

主要模块：

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

### 3.3 route 模块

页面路由模块采用 `contract` / `scene` 拆分：

- `contract`：暴露 typed route key、轻量资源和跨模块最小契约。
- `scene`：包含 Compose 页面、ViewModel、页面级 DI 和 Navigation entry。

当前 route 模块：

```text
:route:test1:contract
:route:test1:scene
:route:test2:contract
:route:test2:scene
:route:test3:contract
:route:test3:scene
:route:search:contract
:route:search:scene
:route:settings:contract
:route:settings:scene
```

当前页面状态：

| 页面 | contract | scene | 说明 |
| --- | --- | --- | --- |
| 测试一 | 有 | 有 | 顶层导航页面壳 |
| 测试二 | 有 | 有 | 顶层导航页面壳 |
| 测试三 | 有 | 有 | 顶层导航页面壳 |
| Search | 有 | 有 | 搜索页，使用本地测试内容 |
| Settings | 有 | 有 | 设置页，读写用户深色模式和语言偏好 |

### 3.4 build-logic 模块

`build-logic/convention` 定义项目自有 Gradle convention plugins，包括：

```text
nowtest.android.application
nowtest.android.application.compose
nowtest.android.application.flavors
nowtest.android.application.jacoco
nowtest.android.route
nowtest.android.route.contract
nowtest.android.route.scene
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

## 4. 导航

项目使用 Navigation 3 和 typed route key：

- `Test1Route`、`Test2Route`、`Test3Route`、`SearchRoute`、`SettingsRoute` 表示页面。
- `core:navigation` 提供 `NavigationState` 和 `Navigator`。
- `route:*:scene` 通过 `EntryProviderScope<NavKey>` 扩展函数注册页面 entry。
- `app` 模块统一组装 `test1Entry()`、`test2Entry()`、`test3Entry()`、`searchEntry()`、`settingsEntry()`。

## 5. 数据与基础设施边界

基础设施组件本身需要保留，例如 DataStore、Room、Repository、Network、DI、Hilt、KSP、导航、测试底座和设计系统。组件内部字段、表、模型、接口、绑定和测试数据是否保留，应以当前功能是否实际使用为准。

当前真实数据使用面较窄：

- 设置页使用 `UserDataRepository` 和 DataStore 保存深色模式偏好。
- 设置页使用 `AppLanguageRepository` 与 `AppCompatDelegate` 管理应用语言，不新增 DataStore 语言字段。
- 搜索页使用本地测试内容，不依赖远程搜索接口。
- Room 和 Network 作为基础设施保留，按后续产品需求接入具体业务。

## 6. 测试体系

项目保留以下测试能力：

- JUnit / Kotlin Test / Truth / Turbine。
- Compose UI Test。
- Roborazzi 截图测试。
- app 层截图基准存放在 `app/src/testDemo/screenshots/`，预期 UI 变化需要录制并验证新基准。
- Hilt 测试替身。
- Macrobenchmark / Baseline Profile。
- 自定义 lint。

常用验证命令：

```bash
./gradlew spotlessCheck
./gradlew testDemoDebugUnitTest
./gradlew :app:assembleDemoDebug
./gradlew verifyRoborazziDemoDebug
```

## 7. 阅读入口

建议按以下顺序阅读源码：

1. `settings.gradle.kts`
2. `gradle/libs.versions.toml`
3. `build-logic/convention`
4. `app/build.gradle.kts`
5. `MainActivity`
6. `NtApp` / `NtAppState`
7. `core:navigation`
8. `route/*/contract` 与 `route/*/scene`
9. `core:data` / `core:datastore` / `core:database` / `core:network`
10. `core:testing`、`core:data-test`、`core:datastore-test`
