# Saved 功能定位文档

项目里的 `Saved` 功能实际对应 `bookmarks` 模块。它的核心含义是新闻资源的 bookmark 状态：用户在新闻卡片上点击保存后，新闻 id 会写入 DataStore；`Saved` 页面再读取这些已保存 id 并展示对应新闻列表。

## 主功能模块

- API 导航 key：`feature/bookmarks/api/src/main/kotlin/com/lhzkml/nowtest/feature/bookmarks/api/navigation/BookmarksNavKey.kt`
- API 文案与标题：`feature/bookmarks/api/src/main/res/values/strings.xml`
- 空状态图片：`feature/bookmarks/api/src/main/res/drawable/feature_bookmarks_api_mg_empty_bookmarks.xml`
- 实现入口：`feature/bookmarks/impl/src/main/kotlin/com/lhzkml/nowtest/feature/bookmarks/impl/navigation/BookmarksEntryProvider.kt`
- ViewModel：`feature/bookmarks/impl/src/main/kotlin/com/lhzkml/nowtest/feature/bookmarks/impl/BookmarksViewModel.kt`
- 页面 UI：`feature/bookmarks/impl/src/main/kotlin/com/lhzkml/nowtest/feature/bookmarks/impl/BookmarksScreen.kt`

## App 入口

- 顶层 tab 定义在 `app/src/main/kotlin/com/lhzkml/nowtest/navigation/TopLevelNavItem.kt`
- `BOOKMARKS` 使用 `NtIcons.Bookmarks` / `NtIcons.BookmarksBorder`
- 底栏文字和 top app bar 标题来自 `feature_bookmarks_api_title`，显示为 `Saved`
- 顶层路由注册：`BookmarksNavKey to BOOKMARKS`
- 页面注册在 `app/src/main/kotlin/com/lhzkml/nowtest/ui/NtApp.kt` 的 `NavDisplay` entry provider 中：`bookmarksEntry(navigator)`
- `app/src/main/kotlin/com/lhzkml/nowtest/ui/NtAppState.kt` 会根据 `observeAllBookmarked()` 判断 Saved tab 是否显示未读提示点

## Saved 页面行为

- `BookmarksViewModel.feedUiState` 通过 `UserNewsResourceRepository.observeAllBookmarked()` 读取已保存新闻
- 页面状态包括 loading、feed、empty
- Feed testTag：`bookmarks:feed`
- Empty testTag：`bookmarks:empty`
- Loading testTag：`bookmarks:loading`
- 用户在 Saved 页面取消保存时，`BookmarksScreen` 通过 `removeFromBookmarks` 调到 `BookmarksViewModel.removeFromSavedResources()`
- `removeFromSavedResources()` 会调用 `UserDataRepository.setNewsResourceBookmarked(newsResourceId, false)`
- 取消保存后会显示 snackbar：`Bookmark removed`，操作按钮为 `UNDO`
- 用户点击 undo 时，`BookmarksViewModel.undoBookmarkRemoval()` 会把最后移除的新闻重新设置为 bookmarked
- 页面停止时，`Lifecycle.Event.ON_STOP` 会清理 undo 状态
- 点击新闻卡片会打开 Custom Tab，并调用 viewed 状态更新
- 点击新闻 topic 会通过 `BookmarksEntryProvider` 里的 `navigator::navigateToTopic` 进入 topic 详情

## 数据链路

- 用户数据模型：`core/model/src/main/kotlin/com/lhzkml/nowtest/core/model/data/UserData.kt`
  - 字段：`bookmarkedNewsResources: Set<String>`
- 新闻 UI 模型：`core/model/src/main/kotlin/com/lhzkml/nowtest/core/model/data/UserNewsResource.kt`
  - 字段：`isSaved: Boolean`
  - 计算方式：`newsResource.id in userData.bookmarkedNewsResources`
- 写入接口：`core/data/src/main/kotlin/com/lhzkml/nowtest/core/data/repository/UserDataRepository.kt`
  - `setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean)`
- 读取接口：`core/data/src/main/kotlin/com/lhzkml/nowtest/core/data/repository/UserNewsResourceRepository.kt`
  - `observeAllBookmarked(): Flow<List<UserNewsResource>>`
- 读取实现：`core/data/src/main/kotlin/com/lhzkml/nowtest/core/data/repository/CompositeUserNewsResourceRepository.kt`
  - 先从 `userData.bookmarkedNewsResources` 取 bookmarked id
  - 如果为空，返回空列表
  - 如果非空，用 `NewsResourceQuery(filterNewsIds = bookmarkedNewsResources)` 查询新闻资源
- 写入实现：`core/data/src/main/kotlin/com/lhzkml/nowtest/core/data/repository/OfflineFirstUserDataRepository.kt`
  - 调用 `NtPreferencesDataSource.setNewsResourceBookmarked()`
  - 同时记录 analytics：`news_resource_saved` / `news_resource_unsaved`
- DataStore 写入：`core/datastore/src/main/kotlin/com/lhzkml/nowtest/core/datastore/NtPreferencesDataSource.kt`
  - 保存时：`bookmarkedNewsResourceIds.put(newsResourceId, true)`
  - 取消保存时：`bookmarkedNewsResourceIds.remove(newsResourceId)`
- Proto 字段：`core/datastore-proto/src/main/proto/com/lhzkml/nowtest/data/user_preferences.proto`
  - `map<string, bool> bookmarked_news_resource_ids = 15;`
- 迁移逻辑：`core/datastore/src/main/kotlin/com/lhzkml/nowtest/core/datastore/ListToMapMigration.kt`
  - 把旧的 `deprecated_bookmarked_news_resource_ids` list 迁移到 `bookmarked_news_resource_ids` map

## 保存按钮与共享 UI

- 新闻卡片：`core/ui/src/main/kotlin/com/lhzkml/nowtest/core/ui/NewsResourceCard.kt`
- 保存按钮 composable：`BookmarkButton`
- 未保存状态 icon：`NtIcons.BookmarkBorder`
- 已保存状态 icon：`NtIcons.Bookmark`
- 无障碍文案：
  - `core_ui_bookmark`
  - `core_ui_unbookmark`
- Feed 组装逻辑：`core/ui/src/main/kotlin/com/lhzkml/nowtest/core/ui/NewsFeed.kt`
  - 使用 `userNewsResource.isSaved` 控制按钮状态
  - 点击时回调 `onNewsResourcesCheckedChanged(userNewsResource.id, !userNewsResource.isSaved)`

## 其他入口也会改变 Saved 状态

- For You 页面：
  - `feature/foryou/impl/src/main/kotlin/com/lhzkml/nowtest/feature/foryou/impl/ForYouViewModel.kt`
  - `updateNewsResourceSaved()` 调用 `setNewsResourceBookmarked()`
- Search 页面：
  - `feature/search/impl/src/main/kotlin/com/lhzkml/nowtest/feature/search/impl/SearchViewModel.kt`
  - `setNewsResourceBookmarked()` 调用 `UserDataRepository.setNewsResourceBookmarked()`
- Topic 详情页：
  - `feature/topic/impl/src/main/kotlin/com/lhzkml/nowtest/feature/topic/impl/TopicViewModel.kt`
  - `bookmarkNews()` 调用 `setNewsResourceBookmarked()`

## 测试与性能文件

- ViewModel 测试：`feature/bookmarks/impl/src/test/kotlin/com/lhzkml/nowtest/feature/bookmarks/impl/BookmarksViewModelTest.kt`
- Compose UI 测试：`feature/bookmarks/impl/src/androidTest/kotlin/com/lhzkml/nowtest/feature/bookmarks/impl/BookmarksScreenTest.kt`
- App 导航测试：`app/src/androidTest/kotlin/com/lhzkml/nowtest/ui/NavigationTest.kt`
- Macrobenchmark 入口：`benchmarks/src/main/kotlin/com/lhzkml/nowtest/bookmarks/BookmarksActions.kt`
- Baseline profile：`benchmarks/src/main/kotlin/com/lhzkml/nowtest/baselineprofile/BookmarksBaselineProfile.kt`

## 总结链路

`BookmarkButton` 点击后，经由 For You、Search、Topic 或 Saved 页面自身的 ViewModel 调用 `UserDataRepository.setNewsResourceBookmarked()`，最终写入 DataStore 的 `bookmarked_news_resource_ids`。`Saved` 页面通过 `UserNewsResourceRepository.observeAllBookmarked()` 读取这些 id，筛选出对应新闻资源并展示。取消保存时支持 snackbar undo。
