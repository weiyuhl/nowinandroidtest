nowtest 应用
============

nowtest 是一款使用 Kotlin 和 Jetpack Compose 构建的原生 Android 应用。项目采用单 Activity、多模块结构，保留现代 Android 应用常用的 UI、导航、数据、依赖注入、测试和构建基础设施。

当前代码重点覆盖应用壳、顶层导航、搜索、设置、设计系统和测试基础设施。文档只描述当前源码中仍然存在并可维护的能力。

## 当前能力

- 顶层页面：保留“测试一”“测试二”“测试三”的导航入口和页面壳，用于维持当前产品导航结构。
- 搜索：使用本地测试内容作为搜索源，覆盖搜索输入、结果展示和 ViewModel 状态流。
- 设置：通过 DataStore 保存深色模式偏好，通过 AppCompatDelegate 管理应用语言，并保留开源许可入口。
- 设计系统：`core:designsystem` 提供 Jasmine 主题、颜色和图标；`core:ui` 提供埋点、Jank、预览、返回文案和时区等跨页面辅助能力。
- 数据基础设施：保留 Repository 模式、DataStore、Room、Network、DI/Hilt/KSP 等组件。

## 开发环境

项目使用 Gradle 构建系统，可直接导入 Android Studio。日常开发建议使用 `demoDebug` 变体。

常用命令：

```bash
./gradlew assembleDemoDebug
./gradlew spotlessApply
./gradlew testDemoDebugUnitTest
./gradlew verifyRoborazziDemoDebug
```

`demo` 和 `prod` 两个产品风味仍保留。日常开发、测试和本地验证优先使用 `demoDebug`；`prod` 变体用于发布或接入真实环境配置时验证。

## 架构

nowtest 遵循 Android 官方架构指南，采用单向数据流：

```text
Compose UI
   ↓ 用户事件
ViewModel
   ↓ StateFlow / Flow
Repository
   ↓
DataStore / Room / Network
```

核心技术栈：

- Jetpack Compose + Material 3
- Navigation 3
- Kotlin coroutines / Flow
- Hilt / KSP
- DataStore / Room
- Retrofit / OkHttp
- Roborazzi
- Macrobenchmark / Baseline Profile
- 自定义 Gradle convention plugins

更详细的架构说明见 [架构学习之旅](docs/ArchitectureLearningJourney.md)。

## 模块化

主应用位于 `app/`，页面路由模块位于 `route/`，共享基础设施和通用能力位于 `core/`。

常见模块类型：

- `app`：应用入口、单 Activity、主题、全局导航和依赖装配。
- `route:*:contract`：页面路由键、轻量资源和跨模块最小契约。
- `route:*:scene`：页面实现、页面级状态管理和 Navigation entry。
- `core:*`：数据、数据库、网络、设计系统、测试、分析、通知、导航等共享组件。
- `build-logic`：项目自定义 Gradle convention plugins。

更多说明见 [模块化学习之旅](docs/ModularizationLearningJourney.md)。

## 测试

项目不依赖 Mock 框架。测试优先使用真实接口的测试替身，并通过 Hilt 测试 API 或构造函数注入替换生产实现。

- 本地测试：JUnit、Kotlin Test、Truth、Turbine、Robolectric。
- UI 测试：Compose TestRule、AndroidX Test。
- 截图测试：Roborazzi。
- 性能测试：Macrobenchmark 与 Baseline Profile。

常用测试命令：

```bash
./gradlew testDemoDebugUnitTest
./gradlew verifyRoborazziDemoDebug
./gradlew connectedDemoDebugAndroidTest
```

Roborazzi 截图基准位于 `app/src/testDemo/screenshots/`。预期 UI 变化需要录制新基准并重新验证；非预期差异应修复代码。
