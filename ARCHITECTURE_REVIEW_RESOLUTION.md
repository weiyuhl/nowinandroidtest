# 架构审阅问题处理记录

本文记录本轮审阅后已经落地的结构调整。旧审阅内容只作为问题来源，不再作为当前架构判断依据。

## 当前结构结论

项目当前采用 `app/`、`core/`、`route/`、`build-logic/`、`benchmarks/`、`lint/`、`ui-test-hilt-manifest/` 的多模块结构。

- `app/`：应用壳、单 Activity、全局导航和页面装配。
- `core/`：独立基础设施和共享能力，不依赖 `app/` 或 `route/`。
- `route/`：页面路由模块，采用 `contract` / `scene` 拆分。
- `build-logic/`：项目自定义 Gradle convention plugins。

## 已处理的问题

- 页面模块从旧的页面模块组织方式迁移为 `route/*/contract|scene`。
- 顶层三个页面改为 `test1`、`test2`、`test3`，显示名称为“测试一”“测试二”“测试三”。
- Navigation 3 typed route key 改为 `Test1Route`、`Test2Route`、`Test3Route`、`SearchRoute`、`SettingsRoute`。
- 页面 entry 改为 `test1Entry()`、`test2Entry()`、`test3Entry()`、`searchEntry()`、`settingsEntry()`。
- Gradle convention plugin 改为 `nowtest.android.route`、`nowtest.android.route.contract`、`nowtest.android.route.scene`。
- Mermaid 模块依赖图改为 `route` 分组和 `android-route` 图例。
- 资源前缀改为当前模块路径对应的 `route_*_contract_` 与 `route_*_scene_`。

## 保留边界

基础设施组件仍然保留，例如 DataStore、Room、Repository、Network、DI、Hilt、KSP、导航、设计系统和测试底座。组件内部字段、表、接口、绑定和测试数据继续按当前实际使用情况裁剪。