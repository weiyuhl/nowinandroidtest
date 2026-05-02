# nowtest 功能修改与新增完整链路文档

本文用于说明在 nowtest 中修改功能、增加功能、删除功能时，应该沿着哪些模块和文件完整处理。它不是架构介绍，而是开发时可执行的检查文档。

核心原则：

- 功能入口、页面 UI、状态、数据、DI、资源、测试、截图测试必须一起检查。
- `DataStore`、`Room`、仓库模式、Hilt、KSP、网络模块、导航模块、设计系统、测试基础设施属于基础设施，不能因为某个业务功能删除就把组件本身删掉。
- 基础设施内部的业务字段、业务表、业务 DTO、业务方法、业务测试数据不属于基础设施。如果功能已经删除，这些内容也要删除。

## 1. 修改或新增功能的总链路

任何功能改动都按这条顺序检查：

```text
产品入口
  -> 导航 Route
  -> EntryProvider
  -> Screen UI
  -> ViewModel / UiState
  -> Repository 接口
  -> Repository 实现
  -> DataStore / Room / Network
  -> Hilt DI 绑定
  -> 资源与多语言
  -> Analytics / Jank / 其他共享能力
  -> 测试替身
  -> 单元测试
  -> Compose UI 测试
  -> app 导航测试
  -> Roborazzi 截图测试
  -> APK 构建
```

不是每个功能都会用到每一层。判断方法如下：

| 功能需要什么 | 必须检查哪里 |
| --- | --- |
| 只是改页面静态 UI | `route/<name>/scene/*Screen.kt`、字符串资源、预览、UI 测试、截图测试 |
| 有输入、加载、选择、保存等状态 | `ViewModel`、`UiState`、ViewModel 单测 |
| 需要保存用户偏好 | `core/model`、`core/datastore-proto`、`core/datastore`、`core/data`、测试替身、DataStore 测试 |
| 需要本地表或复杂查询 | `core/database`、`core/data`、Room schema、DAO 测试、仓库测试 |
| 需要远程请求 | `core/network`、demo/prod 数据源、`core/data`、网络测试、仓库测试 |
| 需要出现在底部/侧边导航 | `TopLevelNavItem.kt`、`NtAppState.kt`、`NtApp.kt`、app 导航测试、app 截图测试 |
| 只是独立二级页面 | route contract、route scene、`NtApp.kt` entryProvider、调用 `navigator.navigate()` 的入口、返回测试 |
| 改主题、导航容器、全局背景 | `core/designsystem`、`NtApp.kt`、app Roborazzi 截图基准 |
| 改设置项 | 设置页 UI、`SettingsViewModel`、对应 Repository/DataStore/语言仓库、设置页测试 |

## 2. 当前模块职责

| 模块 | 当前作用 | 修改功能时的责任 |
| --- | --- | --- |
| `app` | 单 Activity 应用壳、启动、主题注入、顶层导航、全局 snackbar、全局导航测试、app 截图测试 | 新增入口、改顶层导航、改全局壳层行为时必须修改 |
| `route/*/contract` | 页面导航契约，包含 `NavKey` 和跨模块标题资源 | 新增页面或改页面对外入口时修改 |
| `route/*/scene` | 页面 UI、ViewModel、页面状态、页面测试 | 具体业务功能的主要修改位置 |
| `core/model` | 跨模块共享数据模型 | 数据要跨 UI、仓库、DataStore、数据库传递时修改 |
| `core/data` | 仓库接口、仓库实现、数据层 DI、网络/本地组合 | UI 不直接碰 DataStore、Room、Network，统一经过这里 |
| `core/datastore` | DataStore serializer、DataSource、DI | 用户偏好、设置项、轻量持久化状态使用这里 |
| `core/datastore-proto` | DataStore proto 定义 | 新增或删除用户偏好字段时修改 |
| `core/database` | Room 数据库组件、Entity、DAO、schema | 本地结构化数据使用这里 |
| `core/network` | 网络基础设施、demo/prod 数据源、OkHttp、Json、ImageLoader | 远程接口和网络数据源使用这里 |
| `core/designsystem` | Jasmine 主题、颜色、图标 | 全局视觉、主题、图标从这里统一维护 |
| `core/ui` | 跨页面 UI 辅助能力、埋点、Jank、预览工具 | 多页面复用能力放这里，单页面组件不要放这里 |
| `core/navigation` | Navigation 3 状态管理和 `Navigator` | 导航机制本身修改时才动 |
| `core/testing` | 通用测试替身和测试工具 | 仓库接口、全局测试依赖变化时同步 |
| `core/data-test` | data 层 demo/test 替身 | repository 接口变化时同步 |
| `core/datastore-test` | DataStore 测试替身 | DataStore 依赖测试时使用 |
| `core/screenshot-testing` | Roborazzi 截图测试公共配置 | 截图测试基础设施，不是业务功能 |
| `ui-test-hilt-manifest` | Hilt UI 测试 Activity | app/route UI 测试需要 Hilt Activity 时使用 |
| `benchmarks` | Macrobenchmark 和 Baseline Profile | 改启动、顶层路径、关键性能路径时检查 |
| `build-logic` | Gradle convention plugins、Spotless、module graph、GMD、Jacoco | 新模块类型、构建规则、格式化规则变化时修改 |

## 3. 当前功能链路

### 3.1 应用启动和全局壳层

链路：

```text
NtApplication
  -> DebugCrashLogInstaller
  -> ProfileVerifierLogger
  -> MainActivity
  -> MainActivityViewModel
  -> UserDataRepository
  -> NtTheme
  -> rememberNtAppState
  -> NtApp
```

关键文件：

- `app/src/main/kotlin/com/lhzkml/nowtest/NtApplication.kt`
- `app/src/main/kotlin/com/lhzkml/nowtest/MainActivity.kt`
- `app/src/main/kotlin/com/lhzkml/nowtest/MainActivityViewModel.kt`
- `app/src/main/kotlin/com/lhzkml/nowtest/ui/NtApp.kt`
- `app/src/main/kotlin/com/lhzkml/nowtest/ui/NtAppState.kt`
- `app/src/main/kotlin/com/lhzkml/nowtest/util/UiExtensions.kt`

修改影响：

- 改启动、Splash、全局主题、深色模式、系统栏颜色、全局 snackbar，都要检查这里。
- 改 `MainActivityViewModel` 读取的 `UserData` 字段时，要同步 `core/model`、DataStore、Repository 和测试替身。

### 3.2 顶层导航

链路：

```text
TopLevelNavItem.kt
  -> rememberNavigationState(startKey = Test1Route)
  -> NtApp NavigationSuiteScaffold
  -> Navigator.navigate(topLevelRoute)
  -> NavDisplay
  -> route/testX/scene EntryProvider
```

关键文件：

- `app/src/main/kotlin/com/lhzkml/nowtest/navigation/TopLevelNavItem.kt`
- `app/src/main/kotlin/com/lhzkml/nowtest/ui/NtAppState.kt`
- `app/src/main/kotlin/com/lhzkml/nowtest/ui/NtApp.kt`
- `core/navigation/src/main/kotlin/com/lhzkml/nowtest/core/navigation/NavigationState.kt`
- `core/navigation/src/main/kotlin/com/lhzkml/nowtest/core/navigation/Navigator.kt`
- `route/test1/contract/.../Test1Route.kt`
- `route/test2/contract/.../Test2Route.kt`
- `route/test3/contract/.../Test3Route.kt`

测试：

- `app/src/androidTest/kotlin/com/lhzkml/nowtest/ui/NavigationTest.kt`
- `app/src/testDemo/kotlin/com/lhzkml/nowtest/ui/NtAppScreenSizesScreenshotTests.kt`
- `app/src/testDemo/screenshots/*.png`

只要顶层导航数量、标题、图标、选中态、容器背景、顶部导航栏、底部栏/侧边栏布局变化，都要检查 app 导航测试和 app 截图测试。

### 3.3 搜索功能

当前搜索只搜索本地测试内容，不走网络、不走 Room、不走 FTS。

链路：

```text
顶部搜索按钮
  -> navigator.navigate(SearchRoute)
  -> SearchEntryProvider
  -> SearchScreen
  -> SearchViewModel
  -> SavedStateHandle searchQuery
  -> SearchTestContent.localSearchTestContents
  -> SearchResultUiState
```

关键文件：

- `route/search/contract/src/main/kotlin/.../SearchRoute.kt`
- `route/search/contract/src/main/res/values/strings.xml`
- `route/search/contract/src/main/res/values-zh-rCN/strings.xml`
- `route/search/scene/src/main/kotlin/.../navigation/SearchEntryProvider.kt`
- `route/search/scene/src/main/kotlin/.../SearchScreen.kt`
- `route/search/scene/src/main/kotlin/.../SearchViewModel.kt`
- `route/search/scene/src/main/kotlin/.../SearchResultUiState.kt`
- `route/search/scene/src/main/kotlin/.../SearchTestContent.kt`
- `route/search/scene/src/test/kotlin/.../SearchViewModelTest.kt`

修改搜索时：

- 改搜索输入规则：改 `SearchViewModel` 和 `SearchViewModelTest`。
- 改本地搜索内容：改 `SearchTestContent.kt`、搜索字符串资源、`SearchViewModelTest`。
- 改搜索 UI：改 `SearchScreen.kt`，必要时补 Compose UI 测试或截图测试。
- 改成远程搜索：新增 `core/network` 方法、demo/prod 实现、`core/data` repository，再改 `SearchViewModel`。
- 改成数据库搜索：新增 Room Entity/DAO/query、repository，再改 `SearchViewModel`。

### 3.4 设置功能

链路：

```text
顶部设置按钮
  -> navigator.navigate(SettingsRoute)
  -> SettingsEntryProvider
  -> SettingsScreen
  -> SettingsViewModel
  -> UserDataRepository.userData
  -> AppLanguageRepository.appLanguage
```

关键文件：

- `route/settings/contract/src/main/kotlin/.../SettingsRoute.kt`
- `route/settings/scene/src/main/kotlin/.../SettingsScreen.kt`
- `route/settings/scene/src/main/kotlin/.../SettingsViewModel.kt`
- `route/settings/scene/src/main/kotlin/.../AppLanguageRepository.kt`
- `route/settings/scene/src/main/kotlin/.../di/AppLanguageModule.kt`
- `route/settings/scene/src/main/res/values/strings.xml`
- `route/settings/scene/src/main/res/values-zh-rCN/strings.xml`
- `route/settings/scene/src/test/kotlin/.../SettingsViewModelTest.kt`
- `route/settings/scene/src/androidTest/kotlin/.../SettingsScreenTest.kt`

设置里的深色模式链路：

```text
SettingsScreen radio
  -> SettingsViewModel.updateDarkThemeConfig()
  -> UserDataRepository.setDarkThemeConfig()
  -> OfflineFirstUserDataRepository
  -> NtPreferencesDataSource.setDarkThemeConfig()
  -> user_preferences.proto dark_theme_config
  -> MainActivityViewModel.userData
  -> MainActivity shouldUseDarkTheme()
  -> NtTheme(darkTheme)
```

设置里的语言链路：

```text
SettingsScreen radio
  -> SettingsViewModel.updateLanguage()
  -> AppLanguageRepository.setAppLanguage()
  -> AppCompatDelegate.setApplicationLocales()
  -> AppCompat autoStoreLocales metadata
```

语言相关 Manifest：

- `app/src/main/AndroidManifest.xml`

测试：

- 深色模式 ViewModel 状态：`SettingsViewModelTest`
- 设置页面选项展示和点击：`SettingsScreenTest`
- 如果设置入口或返回行为变化：`NavigationTest`

## 4. 新增顶层功能页面的完整步骤

顶层页面是会出现在底部导航/侧边导航里的页面。

### 4.1 新增 contract 模块

新增：

```text
route/<name>/contract/build.gradle.kts
route/<name>/contract/src/main/kotlin/com/lhzkml/nowtest/route/<name>/contract/navigation/<Name>Route.kt
route/<name>/contract/src/main/res/values/strings.xml
route/<name>/contract/src/main/res/values-zh-rCN/strings.xml
```

`build.gradle.kts` 使用：

```kotlin
plugins {
    alias(libs.plugins.nowtest.android.route.contract)
}
```

Route 使用：

```kotlin
@Serializable
object <Name>Route : NavKey
```

contract 只放跨模块契约：Route、导航标题、入口文案。不要把页面 UI 或仓库放在 contract。

### 4.2 新增 scene 模块

新增：

```text
route/<name>/scene/build.gradle.kts
route/<name>/scene/src/main/kotlin/.../<Name>Screen.kt
route/<name>/scene/src/main/kotlin/.../navigation/<Name>EntryProvider.kt
route/<name>/scene/src/main/res/values/strings.xml
route/<name>/scene/src/main/res/values-zh-rCN/strings.xml
```

`build.gradle.kts` 通常使用：

```kotlin
plugins {
    alias(libs.plugins.nowtest.android.route.scene)
    alias(libs.plugins.nowtest.android.library.compose)
}

dependencies {
    implementation(projects.route.<name>.contract)
}
```

如果有页面状态，再新增：

```text
<Name>ViewModel.kt
<Name>UiState.kt
src/test/kotlin/.../<Name>ViewModelTest.kt
```

如果有页面交互测试，再新增：

```text
src/androidTest/kotlin/.../<Name>ScreenTest.kt
```

如果页面需要数据层能力，再按实际需要添加依赖，例如：

```kotlin
dependencies {
    implementation(projects.core.data)

    testImplementation(projects.core.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}
```

不要依赖 `app` 模块。route scene 只能依赖自己的 contract、`core/*` 基础设施和必要第三方库。

### 4.3 接入 Gradle 模块

修改 `settings.gradle.kts`：

```kotlin
include(":route:<name>:contract")
include(":route:<name>:scene")
```

修改 `app/build.gradle.kts`：

```kotlin
implementation(projects.route.<name>.contract)
implementation(projects.route.<name>.scene)
```

### 4.4 接入 app 顶层导航

修改 `TopLevelNavItem.kt`：

- import `<Name>Route`
- 新增 `TopLevelNavItem`
- 加入 `TOP_LEVEL_NAV_ITEMS`
- 图标使用 `NtIcons`

如果缺图标，先改：

- `core/designsystem/src/main/kotlin/com/lhzkml/nowtest/core/designsystem/icon/NtIcons.kt`

### 4.5 接入 Navigation 3 Entry

修改 `NtApp.kt`：

- import `<name>Entry`
- 在 `entryProvider { ... }` 中调用 `<name>Entry()`

如果要改默认启动页，修改：

- `app/src/main/kotlin/com/lhzkml/nowtest/ui/NtAppState.kt`

把 `rememberNavigationState(Test1Route, ...)` 的 start key 改为新的顶层 Route。

### 4.6 补测试和截图

必须检查：

- `app/src/androidTest/kotlin/com/lhzkml/nowtest/ui/NavigationTest.kt`
  - 新顶层 tab 是否存在。
  - 点击后是否选中。
  - 返回行为是否仍符合预期。
  - 搜索/设置顶部入口是否仍存在。

- `app/src/testDemo/kotlin/com/lhzkml/nowtest/ui/NtAppScreenSizesScreenshotTests.kt`
  - 顶层导航项数量变化通常会影响截图。
  - 如果截图变化是预期，运行 `:app:recordRoborazziDemoDebug` 更新基准图。

- `benchmarks/`
  - 如果新页面替代启动页或关键路径，更新 baseline profile 和 macrobenchmark 路径。

## 5. 新增非顶层独立页面的完整步骤

非顶层页面不出现在底部导航/侧边导航里，例如当前搜索和设置。

完整链路：

```text
入口按钮
  -> navigator.navigate(<Name>Route)
  -> route/<name>/contract <Name>Route
  -> route/<name>/scene <Name>EntryProvider
  -> <Name>Screen(onBackClick = navigator.goBack)
```

需要修改：

1. 新增或修改 `route/<name>/contract`。
2. 新增或修改 `route/<name>/scene`。
3. `route/<name>/scene/build.gradle.kts` 必须 `implementation(projects.route.<name>.contract)`。
4. `settings.gradle.kts` include 两个模块。
5. `app/build.gradle.kts` 添加两个模块依赖。
6. `NtApp.kt` 的 `entryProvider` 添加 `<name>Entry(navigator)`。
7. 在入口 UI 中调用 `navigator.navigate(<Name>Route)`。
8. 页面内必须有返回入口或系统返回逻辑可用。

测试：

- 入口点击测试放到 `NavigationTest` 或拥有入口的页面测试。
- 页面 UI 交互测试放到 `route/<name>/scene/src/androidTest`。
- 页面 ViewModel 测试放到 `route/<name>/scene/src/test`。
- 如果页面视觉单独很重要，可以新增 route 层截图测试；如果影响 app 壳层，更新 app 截图测试。

## 6. 修改已有页面功能的完整步骤

优先在页面自己的 scene 模块修改。

检查顺序：

```text
Screen.kt
  -> UiState
  -> ViewModel
  -> Repository
  -> DataStore / Room / Network
  -> DI
  -> strings
  -> tests
```

具体规则：

- 只改布局、按钮、文本：改 `Screen.kt` 和字符串资源，然后检查 UI 测试和截图。
- 改页面状态：改 `UiState`、`ViewModel`、ViewModel 单测。
- 页面需要读写数据：不要在 UI 直接访问 DataStore、Room、Network，先走 repository。
- 页面功能只属于该页面：代码留在 `route/<name>/scene`。
- 多页面复用：再考虑下沉到 `core/ui`、`core/model`、`core/data`。

## 7. 新增或修改用户偏好的完整数据链路

用户偏好包括深色模式、语言、开关、默认选项等。

如果偏好需要 DataStore 持久化，完整链路是：

```text
SettingsScreen 或其他 UI
  -> ViewModel
  -> UserDataRepository
  -> OfflineFirstUserDataRepository
  -> NtPreferencesDataSource
  -> user_preferences.proto
  -> UserData
  -> 测试替身
  -> 使用方
```

必须修改：

1. `core/model/src/main/kotlin/.../UserData.kt`
   - 增加字段。

2. 如需 enum：
   - `core/model/src/main/kotlin/.../data/<Xxx>.kt`

3. `core/datastore-proto/src/main/proto/.../user_preferences.proto`
   - 增加字段。
   - 字段编号不能复用旧编号。
   - 更新 `NEXT AVAILABLE ID`。
   - 删除字段时同步删除映射和测试；旧编号不要立刻赋给新语义。需要兼容旧数据时，用 proto `reserved` 保留旧编号和字段名。

4. 如需 proto enum：
   - 新增 proto，或修改现有 proto。

5. `core/datastore/src/main/kotlin/.../NtPreferencesDataSource.kt`
   - proto -> model 映射。
   - `setXxx()` 写入方法。

6. `core/data/src/main/kotlin/.../repository/UserDataRepository.kt`
   - 增加 repository 方法。

7. `core/data/src/main/kotlin/.../repository/OfflineFirstUserDataRepository.kt`
   - 调用 DataSource。
   - 如果需要埋点，调用 `AnalyticsHelper`。

8. 测试替身：
   - `core/testing/src/main/kotlin/.../TestUserDataRepository.kt`
   - `core/testing/src/main/kotlin/.../emptyUserData`
   - `core/data-test/src/main/kotlin/.../FakeUserDataRepository.kt`

9. 使用方：
   - 页面 ViewModel。
   - 如果影响全局壳层，还要改 `MainActivityViewModel.kt` 和 `MainActivity.kt`。

10. 测试：
   - `core/datastore/src/test/.../UserPreferencesSerializerTest.kt`
   - 相关 ViewModel 测试。
   - 相关 UI 测试。

如果偏好使用系统组件自己持久化，例如当前语言使用 `AppCompatDelegate`，则不需要新增 DataStore 字段，但需要检查 Manifest metadata、Repository、ViewModel、设置页 UI 和测试。

## 8. 新增或修改 Room 数据的完整链路

Room 组件是基础设施，业务表和字段不是基础设施。

新增本地表：

```text
Entity
  -> DAO
  -> NtDatabase
  -> DaosModule
  -> Repository
  -> DataModule
  -> ViewModel
  -> tests
```

必须修改：

1. `core/database/src/main/kotlin/.../model/<Xxx>Entity.kt`
2. `core/database/src/main/kotlin/.../dao/<Xxx>Dao.kt`
3. `core/database/src/main/kotlin/.../NtDatabase.kt`
   - `entities = [...]`
   - abstract DAO getter
4. `core/database/src/main/kotlin/.../di/DaosModule.kt`
5. `core/data/src/main/kotlin/.../repository/`
6. `core/data/src/main/kotlin/.../di/DataModule.kt`
7. `core/model` 中需要跨模块暴露的数据模型
8. `core/database/schemas/...`
9. DAO 测试、仓库测试、ViewModel 测试

删除某个业务表时：

- 删除 Entity。
- 删除 DAO。
- 从 `NtDatabase` entities 和 abstract DAO 中移除。
- 从 `DaosModule` 移除 provide/binds。
- 删除 repository 中对应方法。
- 删除测试替身和测试数据。
- 更新 schema。
- 保留 Room 模块、DatabaseModule、数据库组件本身。

## 9. 新增或修改网络功能的完整链路

网络模块是基础设施，具体 API、DTO、字段不是基础设施。

新增接口：

```text
NtNetworkDataSource
  -> DemoNtNetworkDataSource
  -> RetrofitNtNetwork
  -> DTO / serializer
  -> Repository
  -> ViewModel
  -> tests
```

必须修改：

1. `core/network/src/main/kotlin/.../NtNetworkDataSource.kt`
   - 增加数据源方法。

2. `core/network/src/main/kotlin/.../demo/DemoNtNetworkDataSource.kt`
   - demo flavor 的稳定测试数据。

3. `core/network/src/main/kotlin/.../retrofit/RetrofitNtNetwork.kt`
   - prod flavor 的真实实现。

4. 如需 DTO：
   - 放在 `core/network`。
   - DTO 不要泄露给 UI，转换成 `core/model` 或 repository 输出模型。

5. `core/data`
   - repository 组合网络、本地数据和错误处理。

6. DI：
   - 当前 demo/prod 已通过 `FlavoredNetworkModule` 绑定 `NtNetworkDataSource`。
   - 如果新增多个数据源接口，才新增对应绑定。

7. 测试：
   - `core/network/src/test/...`
   - repository 测试。
   - ViewModel 测试。

## 10. 资源、多语言和文案链路

当前 app 支持：

- `en`
- `zh-rCN`

配置位置：

- `app/build.gradle.kts`

```kotlin
androidResources {
    generateLocaleConfig = true
    localeFilters += setOf("en", "zh-rCN")
}
```

资源放置规则：

| 文案类型 | 放在哪里 |
| --- | --- |
| app 名称、全局 snackbar、顶部搜索/设置 contentDescription | `app/src/main/res/values*` |
| 顶层导航标题、跨模块页面标题 | `route/<name>/contract/src/main/res/values*` |
| 页面内部文案 | `route/<name>/scene/src/main/res/values*` |
| core 共享 UI 文案 | `core/ui/src/main/res/values*` |
| 通知文案 | `core/notifications/src/main/res/values*` |

新增或修改文案时：

1. 英文 `values/strings.xml`。
2. 中文 `values-zh-rCN/strings.xml`。
3. 测试中通过 resource id 获取字符串，避免硬编码。
4. contentDescription 改动会影响 UI 测试和无障碍测试。

## 11. 主题、图标和视觉链路

当前主题是 Jasmine 主题，入口：

- `core/designsystem/src/main/kotlin/com/lhzkml/nowtest/core/designsystem/theme/Color.kt`
- `core/designsystem/src/main/kotlin/com/lhzkml/nowtest/core/designsystem/theme/Theme.kt`
- `core/designsystem/src/main/kotlin/com/lhzkml/nowtest/core/designsystem/icon/NtIcons.kt`

链路：

```text
MainActivity
  -> NtTheme(darkTheme)
  -> MaterialTheme.colorScheme
  -> NtApp / route screens
```

修改规则：

- 改颜色直接改 `Color.kt` 和 `Theme.kt`。
- 不再新增主题 token 中间层。
- 页面使用 `MaterialTheme.colorScheme`、`MaterialTheme.typography`。
- 图标通过 `NtIcons` 统一暴露。
- 全局主题变化必须检查 app Roborazzi 截图。
- 仅页面内部视觉变化，优先检查该页面 UI 测试；如果没有单独截图，但影响 app 壳层截图，也要更新 app 截图。

## 12. Hilt、DI、KSP 链路

Hilt 基础链路：

```text
NtApplication @HiltAndroidApp
  -> MainActivity @AndroidEntryPoint
  -> @HiltViewModel
  -> Repository / DataSource / Monitor / Analytics
  -> Module @Binds / @Provides
```

常见 DI 文件：

- `core/data/src/main/kotlin/.../di/DataModule.kt`
- `core/datastore/src/main/kotlin/.../di/DataStoreModule.kt`
- `core/database/src/main/kotlin/.../di/DatabaseModule.kt`
- `core/database/src/main/kotlin/.../di/DaosModule.kt`
- `core/network/src/main/kotlin/.../di/NetworkModule.kt`
- `core/network/src/demo|prod/kotlin/.../di/FlavoredNetworkModule.kt`
- `core/analytics/src/demo|prod/kotlin/.../AnalyticsModule.kt`
- `route/settings/scene/src/main/kotlin/.../di/AppLanguageModule.kt`

修改接口或实现时：

- repository 接口变了，所有实现和测试替身都要同步。
- 新增 constructor 注入依赖时，确认依赖已有 Hilt 绑定。
- 新增 KSP 相关能力时，确认模块插件包含 Hilt/KSP 或 Room convention。
- demo/prod flavor 分开实现时，两个 flavor 都要能编译。

## 13. 普通测试链路

功能改动至少要找到对应测试层，不是只跑一个 assemble。

| 改动类型 | 应该检查的测试 |
| --- | --- |
| 页面 ViewModel 状态变化 | `route/<name>/scene/src/test/...ViewModelTest.kt` |
| 设置项变化 | `SettingsViewModelTest`、`SettingsScreenTest` |
| 搜索规则变化 | `SearchViewModelTest` |
| 顶层导航变化 | `app/src/androidTest/.../NavigationTest.kt` |
| Navigation 3 行为变化 | `core/navigation/src/test/.../NavigatorTest.kt` |
| DataStore 字段变化 | `core/datastore/src/test/.../UserPreferencesSerializerTest.kt` |
| Repository 接口变化 | `core/data` 测试、`core/testing`、`core/data-test` |
| Network 数据源变化 | `core/network/src/test/.../NtNetworkDataSourceTest.kt` |
| 设计系统主题变化 | `core/designsystem/src/test/.../ThemeTest.kt` |
| lint 规则变化 | `lint/src/test/.../TestMethodNameDetectorTest.kt` |

常用命令：

```powershell
.\gradlew.bat spotlessCheck
.\gradlew.bat :route:search:scene:testDemoDebugUnitTest
.\gradlew.bat :route:settings:scene:testDemoDebugUnitTest
.\gradlew.bat :core:navigation:testDemoDebugUnitTest
.\gradlew.bat :core:datastore:testDemoDebugUnitTest
.\gradlew.bat :core:designsystem:testDemoDebugUnitTest
.\gradlew.bat :app:testDemoDebugUnitTest
```

如果改动涉及 Hilt Android UI 测试：

```powershell
.\gradlew.bat :route:settings:scene:connectedDemoDebugAndroidTest
.\gradlew.bat :app:connectedDemoDebugAndroidTest
```

项目也配置了 Gradle Managed Devices，可按实际需要运行对应 GMD 任务。

## 14. 截图测试链路

截图测试不是所有页面都有，但以下情况必须检查：

| 改动 | 检查位置 |
| --- | --- |
| 顶层导航数量、图标、label、选中态变化 | `app/src/testDemo/kotlin/.../NtAppScreenSizesScreenshotTests.kt` |
| 顶部导航栏背景、高度、按钮位置变化 | app Roborazzi 截图 |
| `NavigationSuiteScaffold`、底部栏、侧边栏变化 | app Roborazzi 截图 |
| 全局主题、背景、surface、深色模式变化 | app Roborazzi 截图和 `core/designsystem` 测试 |
| snackbar 位置、insets、颜色变化 | `SnackbarScreenshotTests.kt`、`SnackbarInsetsScreenshotTests.kt` |
| 截图测试工具配置变化 | `core/screenshot-testing` |

验证截图：

```powershell
.\gradlew.bat :app:verifyRoborazziDemoDebug
```

更新截图基准：

```powershell
.\gradlew.bat :app:recordRoborazziDemoDebug
```

规则：

- 如果 UI 变化是预期结果，更新截图基准。
- 如果 UI 变化不是预期结果，修代码，不要删除测试。
- 不要把截图测试当成兼容旧 UI 的地方。UI 已变，测试和基准就应跟着真实 UI 改。

## 15. 删除或裁剪功能的完整链路

删除功能时，按这个顺序查，不要只删页面：

```text
入口
  -> Route
  -> EntryProvider
  -> Screen
  -> ViewModel
  -> UiState
  -> strings
  -> repository API
  -> repository impl
  -> DataStore field
  -> Room table / DAO
  -> Network API / DTO
  -> DI binding
  -> fake/test data
  -> unit tests
  -> UI tests
  -> screenshot baselines
  -> docs
```

具体位置：

1. 入口
   - `TopLevelNavItem.kt`
   - `NtApp.kt`
   - 页面内按钮或顶部导航按钮

2. route 模块
   - `route/<name>/contract`
   - `route/<name>/scene`
   - `settings.gradle.kts`
   - `app/build.gradle.kts`

3. 页面实现
   - `Screen.kt`
   - `ViewModel.kt`
   - `UiState.kt`
   - Preview
   - `values/strings.xml`
   - `values-zh-rCN/strings.xml`

4. 数据层业务内容
   - `core/model` 业务模型和字段
   - `core/datastore-proto` proto 字段
   - `core/datastore` 映射和写入方法
   - `core/database` Entity、DAO、schema
   - `core/network` API、DTO、demo/prod 数据
   - `core/data` repository 方法和实现

5. DI 和测试替身
   - `DataModule`
   - `DaosModule`
   - `FlavoredNetworkModule`
   - `core/testing`
   - `core/data-test`
   - `core/datastore-test`

6. 测试和截图
   - ViewModel 单测
   - Compose UI 测试
   - app 导航测试
   - Roborazzi 截图测试和基准图
   - benchmarks/baseline profile 路径

7. 文档
   - 根目录文档
   - `app/README.md`
   - route/core 模块 README

可以删除业务字段、表、DTO、方法、页面内交互、测试数据。不能误删仍被其他功能使用的基础设施组件。

## 16. 功能开发完成后的完整验证清单

这不是最小清单，是按改动范围选择执行的完整收尾清单。

### 16.1 每次都要检查

- `git status --short` 看清楚本次改动范围。
- `rg` 搜索旧功能名、旧 route 名、旧字段名，确认没有残留。
- `spotlessCheck` 或 `spotlessApply` 后再检查 diff。
- 至少跑受影响模块的单元测试。
- 构建 `:app:assembleDemoDebug`。

### 16.2 改页面时

- 页面 ViewModel 测试是否更新。
- 页面 UI 测试是否更新。
- 字符串是否同时更新英文和中文。
- contentDescription 是否影响测试。
- 预览是否还能编译。

### 16.3 改导航时

- `TopLevelNavItem.kt` 是否正确。
- `NtApp.kt` entryProvider 是否正确。
- `NavigationTest` 是否更新。
- app Roborazzi 导航截图是否需要更新。
- benchmarks 里的关键路径是否仍有效。

### 16.4 改 DataStore / Repository 时

- proto 字段编号是否正确。
- `UserData` 是否同步。
- `NtPreferencesDataSource` 映射是否同步。
- `UserDataRepository` 所有实现是否同步。
- `TestUserDataRepository`、`FakeUserDataRepository` 是否同步。
- DataStore serializer 测试是否更新。
- 使用该字段的 ViewModel 测试是否更新。

### 16.5 改 Room 时

- Entity、DAO、Database、DaosModule 是否同步。
- schema 是否同步。
- repository 是否同步。
- DAO/Repository 测试是否同步。

### 16.6 改 Network 时

- interface、demo 实现、prod 实现是否同步。
- DTO 是否只停留在 network/data 层。
- repository 是否输出 UI 可用模型。
- network/repository/ViewModel 测试是否同步。

### 16.7 改主题或全局 UI 时

- `core/designsystem` 测试是否跑过。
- app Roborazzi 是否验证。
- 如果截图差异符合预期，是否录制新基准。

### 16.8 常用收尾命令

```powershell
.\gradlew.bat spotlessCheck
.\gradlew.bat :route:search:scene:testDemoDebugUnitTest
.\gradlew.bat :route:settings:scene:testDemoDebugUnitTest
.\gradlew.bat :app:testDemoDebugUnitTest
.\gradlew.bat :app:verifyRoborazziDemoDebug
.\gradlew.bat :app:assembleDemoDebug
```

如果截图基准需要更新：

```powershell
.\gradlew.bat :app:recordRoborazziDemoDebug
.\gradlew.bat :app:verifyRoborazziDemoDebug
```
