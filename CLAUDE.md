# CLAUDE.md

本文件为 Claude Code（claude.ai/code）在此仓库中工作时提供指导。

## 构建命令

- 构建：`./gradlew assembleDemoDebug`
- 修复格式/代码检查：`./gradlew spotlessApply`
- 运行所有本地测试：`./gradlew testDemoDebug`
- 运行单个测试类：`./gradlew testDemoDebug --tests "com.lhzkml.nowtest.MyTestClass"`
- 运行所有插桩测试：`./gradlew connectedDemoDebugAndroidTest`
- 截图测试（验证）：`./gradlew verifyRoborazziDemoDebug`
- 截图测试（录制新基线）：`./gradlew recordRoborazziDemoDebug`
- Compose 编译器指标：`./gradlew assembleRelease -PenableComposeCompilerMetrics=true -PenableComposeCompilerReports=true`

日常开发优先使用 `demoDebug` 变体。不要直接运行 `./gradlew test` 或 `./gradlew connectedAndroidTest`，因为这会运行所有变体并增加无关失败风险。

## 架构

单 Activity 响应式应用，采用单向数据流（UDF）模式：

- **UI 层：** Jetpack Compose + Material 3。ViewModel 通过 Kotlin `Flow` 暴露状态，界面观察状态并将事件传回 ViewModel。
- **领域层：** 放置跨仓库组合逻辑或页面用例，按当前业务需要维护。
- **数据层：** 使用仓库模式封装 DataStore、Room、Network 等数据源。

核心库：Hilt、Navigation 3、Room、Proto DataStore、Retrofit/OkHttp、Roborazzi、Macrobenchmark、Baseline Profile。

## 模块结构

- `app/`：应用模块，包含 `MainActivity`、`NtApp`、顶层导航和应用级依赖装配。
- `feature/<name>/api`：功能的公开接口，通常只包含导航键。
- `feature/<name>/impl`：功能实现，可以依赖其他 feature 的 `api` 模块。
- `core/<name>`：共享库，不依赖 feature 或 app 模块。包括 `data`、`database`、`network`、`model`、`designsystem`、`ui`、`navigation`、`domain`、`testing`、`analytics`、`notifications` 等。
- `build-logic/`：包含约定插件的复合构建，例如 `nowtest.android.application`、`nowtest.android.library.compose`。
- `benchmarks/`：宏基准测试和基线配置文件生成。

约定插件采用可叠加、可组合的设计。一次性的构建逻辑应直接写在模块的 `build.gradle.kts` 中，而不是创建新的约定插件。

## 构建变体

两种产品 flavor：`demo` 和 `prod`。
两种构建类型：`debug` 和 `release`。
开发使用 `demoDebug`。UI 性能测试使用 `demoRelease`。

## 测试

不使用 Mock 框架。Hilt 的测试 API 注入测试替身，即实现相同接口的真实类，但具有简化行为和测试钩子。

- 本地测试：JUnit + Turbine + Truth。
- 插桩测试：使用 `ComposeTestRule` 配合 `ComponentActivity` 测试 UI 功能。`:app` 模块中的更广泛测试可以启动 `MainActivity`。
- 截图测试：Roborazzi。基线图片在 Linux CI 上录制，在其他平台上可能存在像素差异。

UI 测试与源代码同目录存放：`src/testDemo/` 和 `src/androidTestDemo/`。

## 代码风格

Kotlin 代码风格为 `official`。格式由 Spotless（包含 ktlint）强制执行，CI 会阻止不合规的代码。提交前运行 `./gradlew spotlessApply`。

## 环境要求

需要 JDK 17+。`settings.gradle.kts` 中的 `check` 在配置阶段强制执行此要求。
