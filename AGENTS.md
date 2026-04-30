# nowtest 项目

nowtest 是一款使用 Kotlin 编写的原生 Android 移动应用。它提供关于 Android 开发的定期新闻。用户可以选择关注主题、在有新内容时收到通知以及收藏项目。

## 架构

本项目是一个现代化的 Android 应用，遵循 Google 的官方架构指南。它是一个响应式、单 Activity 应用，使用以下技术：

-   **UI：** 完全使用 Jetpack Compose 构建，包括 Material 3 组件和适配不同屏幕尺寸的自适应布局。
-   **状态管理：** 使用 Kotlin 协程和 `Flow` 实现单向数据流（UDF）。`ViewModel` 作为状态持有者，将 UI 状态以数据流的形式暴露。
-   **依赖注入：** 使用 Hilt 进行全应用的依赖注入，简化依赖管理并提高可测试性。
-   **导航：** 使用 Jetpack Navigation 2 for Compose 处理导航，提供声明式和类型安全的方式来在屏幕之间导航。
-   **数据：** 数据层使用仓库模式实现。
    -   **本地数据：** Room 和 DataStore 用于本地数据持久化。
    -   **远程数据：** Retrofit 和 OkHttp 用于从网络获取数据。
-   **后台处理：** WorkManager 用于可延后的后台任务。

## 模块

主 Android 应用位于 `app/` 文件夹中。功能模块位于 `feature/` 中，核心和共享模块位于 `core/` 中。

## 构建与测试命令

应用和 Android 库有两种产品风味：`demo` 和 `prod`，以及两种构建类型：`debug` 和 `release`。

- 构建：`./gradlew assemble{Variant}`。通常使用 `assembleDemoDebug`。
- 修复代码格式/检查：`./gradlew spotlessApply`
- 运行本地测试：`./gradlew {variant}Test`
- 运行单个测试：`./gradlew {variant}Test --tests "com.example.myapp.MyTestClass"`
- 运行本地截图测试：`./gradlew verifyRoborazziDemoDebug`

### 插桩测试

- Gradle 管理的设备用于运行设备测试：`./gradlew pixel6api31aospDebugAndroidTest`。还有 `pixel4api30aospatdDebugAndroidTest` 和 `pixelcapi30aospatdDebugAndroidTest`。

### 编写测试

#### 插桩测试

- UI 功能的测试应仅使用 `ComposeTestRule` 配合 `ComponentActivity`。
- 更大范围的测试放在 `:app` 模块中，可以启动 `MainActivity` 等 Activity。

#### 本地测试

- [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) 用于大多数断言
- [cashapp/turbine](https://github.com/cashapp/turbine) 用于复杂的协程测试
- [google/truth](https://github.com/google/truth) 用于断言

## 持续集成

- 工作流定义在 `.github/workflows/*.yaml` 中，包含各种检查。
- 截图测试由 CI 生成，因此不应从工作站检入仓库。

## 版本控制与代码位置

- 项目使用 git，托管在 https://github.com/weiyuhl/nowinandroidtest。
