# nowtest 项目

nowtest 是一款使用 Kotlin 编写的原生 Android 移动应用。项目采用 Jetpack Compose、多模块结构和现代 Android 基础设施，当前重点维护应用壳、导航、搜索、设置、设计系统、数据基础设施和测试基础设施。

## 架构

本项目是一个响应式、单 Activity Android 应用，使用以下技术：

- **UI：** 使用 Jetpack Compose 构建，包括 Material 3 组件和适配不同屏幕尺寸的自适应布局。
- **状态管理：** 使用 Kotlin 协程和 `Flow` 实现单向数据流。`ViewModel` 作为状态持有者，将 UI 状态以数据流形式暴露。
- **依赖注入：** 使用 Hilt 进行全应用依赖注入。
- **导航：** 使用 Navigation 3 和类型安全的 `NavKey` 组织页面导航。
- **数据：** 数据层使用仓库模式实现。
    - **本地数据：** Room 和 DataStore 用于本地数据持久化。
    - **远程能力：** Retrofit 和 OkHttp 作为网络基础设施保留。
- **测试：** 使用 JUnit、Truth、Turbine、Compose Test、Roborazzi 和 Hilt 测试替身。

## 模块

主 Android 应用位于 `app/` 文件夹中。页面路由模块位于 `route/` 中，核心和共享模块位于 `core/` 中。

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

- UI 功能测试应使用 `ComposeTestRule` 配合 `ComponentActivity`。
- 更大范围的测试放在 `:app` 模块中，可以启动 `MainActivity`。

#### 本地测试

- kotlinx.coroutines 用于大多数协程断言。
- Turbine 用于复杂的 Flow 测试。
- Truth 用于断言。

## 持续集成

- 工作流定义在 `.github/workflows/*.yaml` 中，包含各种检查。
- 截图测试由 CI 生成，因此不应从工作站检入仓库。

## 版本控制与代码位置

- 项目使用 git，远程仓库地址以当前 `git remote` 配置为准。
