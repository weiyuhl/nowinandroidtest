# 基础设施组件边界说明

本文用于区分 nowtest 项目中“必须保留的基础设施组件”和“可随当前功能调整的业务实现”。

核心规则：

- 基础设施是可复用的机制、模块、运行时能力或工程能力，例如 DataStore、Room 数据库组件、仓库模式、DI/Hilt/KSP、网络模块、导航框架、Jasmine 主题与图标、测试底座。
- 业务字段、业务表、业务 DTO、业务状态和页面专用交互不是基础设施。即使它们位于 DataStore、Room、Repository、DI 或 Network 模块中，也应按当前功能是否使用来判断是否保留。
- 页面是否保留取决于产品要求。保留页面壳时，应保留最小导航和页面入口；页面内部交互、状态流、仓库调用和测试数据按当前功能需要维护。

## 需要保留的基础设施

| 组件 | 路径 | 维护规则 |
| --- | --- | --- |
| 应用壳与单 Activity | `app/src/main/kotlin/.../MainActivity.kt`, `NtApplication.kt`, `ui/NtApp.kt` | 保留应用启动、主题、导航宿主和 Hilt 入口 |
| Gradle 与构建逻辑 | `settings.gradle.kts`, `build.gradle.kts`, `gradle/`, `build-logic/` | 保留构建体系和 convention plugin 机制 |
| Hilt / DI / KSP | `core/*/di/`, `app/di/` | 保留注入体系；绑定内容按当前依赖维护 |
| DataStore | `core/datastore/`, `core/datastore-proto/`, `core/datastore-test/` | 保留 DataStore、Serializer、DI；proto 字段按当前用户设置维护 |
| Room 数据库组件 | `core/database/` | 保留 Room 数据库组件；Entity、DAO 和 schema 按当前数据需求维护 |
| 仓库层模式 | `core/data/src/main/kotlin/.../repository/`, `core/data/di/` | 保留仍被使用的仓库接口、实现和测试替身 |
| 网络模块 | `core/network/` | 保留网络模块、demo/prod 数据源抽象、OkHttp/Json 配置 |
| 通知组件 | `core/notifications/` | 保留通知抽象和 flavor 绑定 |
| 导航基础设施 | `core/navigation/`, `route/*/contract/.../*Route.kt` | 保留导航机制；具体页面入口按产品导航结构维护 |
| 设计系统与共享 UI | `core/designsystem/`, `core/ui/` | 保留 Jasmine 主题、颜色、图标、预览、埋点、Jank、时区和返回文案等跨页面能力；单页面 UI 组件不作为基础设施保留 |
| 测试基础设施 | `core/testing/`, `core:data-test`, `core:datastore-test`, `core:screenshot-testing`, `ui-test-hilt-manifest/` | 保留测试底座；测试替身、测试数据和截图基准按当前接口与 UI 维护 |

## 当前仍保留的数据能力

- 用户设置：`dark_theme_config` 及对应的 `UserDataRepository` 方法。
- 应用语言：当前由 `AppCompatDelegate` 和系统 locale storage 管理，不使用 DataStore 字段。
- Room 数据库组件本身：当前使用 `database_metadata` 作为基础设施 metadata 表。
- 网络模块本身：`NtNetworkDataSource` 作为网络数据源抽象，demo/prod 实现按当前需求提供。

## 检查清单

1. 先判断目标是基础设施组件，还是组件内部的业务字段、表、方法、状态或测试数据。
2. 保留基础设施组件，按当前功能维护组件内部内容。
3. 检查 DataStore proto、Room schema、Repository API、Network DTO、DI module、Fake/test data 是否与当前功能一致。
4. 保留空白页面时，只保留最小导航和页面壳。
5. 运行构建或相关测试，确认没有 KSP、Hilt、Room、proto 生成错误。
