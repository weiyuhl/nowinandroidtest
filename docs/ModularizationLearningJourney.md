# 模块化学习之旅

nowtest 当前保持多模块结构：`app/` 负责应用壳和模块装配，`route/` 放页面路由模块，`core/` 放可复用基础设施和共享能力。

## 当前模块结构

```text
app
benchmarks
build-logic
core
  analytics
  common
  data
  data-test
  database
  datastore
  datastore-proto
  datastore-test
  designsystem
  domain
  model
  navigation
  network
  notifications
  screenshot-testing
  testing
  ui
route
  search
    contract
    scene
  settings
    contract
    scene
  test1
    contract
    scene
  test2
    contract
    scene
  test3
    contract
    scene
lint
ui-test-hilt-manifest
```

## 模块类型

| 模块 | 责任 |
| --- | --- |
| `app` | 应用入口、单 Activity、Splash、全局主题、Navigation 3 装配、顶部导航栏、底部/侧边导航、全局 snackbar、app 层测试 |
| `route:*:contract` | 页面 `NavKey`、跨模块标题和入口资源 |
| `route:*:scene` | 页面 Composable、ViewModel、UiState、页面内资源、页面测试、Navigation entry |
| `core:model` | 跨模块共享模型，例如 `UserData`、`DarkThemeConfig` |
| `core:data` | 仓库接口、仓库实现、数据层 DI、系统状态监控 |
| `core:datastore` | Proto DataStore serializer、DataSource、DataStore DI |
| `core:datastore-proto` | DataStore proto 定义 |
| `core:database` | Room 数据库、Entity、DAO、schema |
| `core:network` | 网络数据源抽象、demo/prod 实现、OkHttp、Json、ImageLoader |
| `core:designsystem` | Jasmine 主题、颜色、图标、设计系统测试 |
| `core:ui` | 跨页面 UI 辅助能力、埋点、Jank、预览工具 |
| `core:navigation` | Navigation 3 状态管理和 `Navigator` |
| `core:analytics` | 埋点接口、NoOp/Stub 实现、Compose Local |
| `core:testing` | 通用测试替身和测试工具 |
| `core:data-test` | data 层测试替身 |
| `core:datastore-test` | DataStore 测试替身 |
| `core:screenshot-testing` | Roborazzi 截图测试公共配置 |
| `benchmarks` | Macrobenchmark 和 Baseline Profile |
| `build-logic` | Gradle convention plugins、Spotless、Jacoco、GMD、模块图任务 |
| `lint` | 自定义 lint 规则 |
| `ui-test-hilt-manifest` | Hilt UI 测试使用的 Activity 壳 |

`core:domain` 当前保留为共享 UseCase 模块。只有当多个调用方需要复用同一段业务编排逻辑时，才应该在这里新增 UseCase。

## 页面路由模块

页面模块按 `contract + scene` 拆分：

```text
route/<name>/contract
  -> <Name>Route.kt
  -> values/strings.xml
  -> values-zh-rCN/strings.xml

route/<name>/scene
  -> <Name>Screen.kt
  -> <Name>ViewModel.kt
  -> <Name>UiState.kt
  -> navigation/<Name>EntryProvider.kt
  -> src/test
  -> src/androidTest
```

当前顶层页面：

- `route:test1`
- `route:test2`
- `route:test3`

当前独立页面：

- `route:search`
- `route:settings`

顶层页面会加入 `TOP_LEVEL_NAV_ITEMS`。独立页面只加入 `NtApp.kt` 的 `entryProvider`，并由按钮调用 `navigator.navigate(route)` 进入。

## 模块依赖方向

推荐依赖方向：

```text
app
  -> route/*/contract
  -> route/*/scene
  -> core/*

route/*/scene
  -> route/*/contract
  -> core:ui
  -> core:designsystem
  -> core:data      (仅在页面需要数据时)

route/*/contract
  -> core:navigation

core:data
  -> core:model
  -> core:datastore
  -> core:database
  -> core:network
  -> core:analytics

core:datastore
  -> core:datastore-proto
  -> core:model

core:database
  -> core:model

core:network
  -> core:model
```

不要让 `core` 依赖 `route` 或 `app`。不要让 route scene 依赖 `app`。

## 新增模块时的检查点

新增 route 页面模块时：

1. 在 `settings.gradle.kts` include `contract` 和 `scene`。
2. 在 `app/build.gradle.kts` 添加两个模块依赖。
3. `scene` 模块必须依赖自己的 `contract` 模块。
4. contract 定义 `@Serializable object <Name>Route : NavKey`。
5. scene 定义 `<Name>EntryProvider`。
6. `NtApp.kt` 的 `entryProvider` 装配 entry。
7. 顶层页面还要修改 `TopLevelNavItem.kt` 和导航测试。
8. 修改资源时同步英文和简体中文。
9. 修改导航或全局视觉时检查 Roborazzi 截图。

新增 core 能力时：

1. 先判断是不是多个模块共享。
2. 如果只属于单个页面，保留在对应 `route/*/scene`。
3. 如果是共享模型，放 `core:model`。
4. 如果是数据读写，优先通过 `core:data` repository 暴露。
5. 如果是持久化偏好，走 `core:datastore` 和 `core:datastore-proto`。
6. 如果是结构化本地数据，走 `core:database`。
7. 如果是远程数据，走 `core:network`。
8. 同步 Hilt DI、测试替身和测试。

## 基础设施边界

以下是基础设施组件，应按当前项目保留：

- Gradle 和 `build-logic`
- Hilt / KSP
- Navigation 3 基础封装
- DataStore 组件
- Room 组件
- Repository 模式
- Network 模块
- DesignSystem 和 UI 工具
- Analytics 接口
- Testing、Data test、Datastore test、Screenshot testing
- Benchmarks 和 Baseline Profile

以下不是基础设施，应按当前功能是否使用来维护：

- DataStore 里的具体业务字段
- Room 里的具体业务表、DAO、schema 字段
- Network 里的具体业务 API、DTO、demo 数据
- Repository 的具体业务方法
- 页面内部 UI、状态和交互
- 功能专用测试数据和测试断言

完整开发流程见根目录 `DEVELOPMENT_CHANGE_GUIDE.md`。
