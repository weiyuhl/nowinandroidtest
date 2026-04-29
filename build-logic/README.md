# 约定插件

`build-logic` 文件夹定义了项目特定的约定插件，用于为通用模块配置保持单一的真实来源。

此方法重度参考了
[https://developer.squareup.com/blog/herding-elephants/](https://developer.squareup.com/blog/herding-elephants/)
和
[https://github.com/jjohannes/idiomatic-gradle](https://github.com/jjohannes/idiomatic-gradle)。

通过在 `build-logic` 中设置约定插件，我们可以避免重复的构建脚本配置和混乱的 `subproject` 配置，同时避开 `buildSrc` 目录的缺陷。

`build-logic` 是一个复合构建，在根
[`settings.gradle.kts`](../settings.gradle.kts) 中配置。

`build-logic` 内部有一个 `convention` 模块，定义了一组插件，所有普通模块可以使用这些插件来配置自身。

`build-logic` 还包含一组用于在插件之间共享逻辑的 `Kotlin` 文件，这在配置 Android 组件（库 vs 应用）时共享代码最为有用。

这些插件是**可叠加**和**可组合**的，尽量只承担单一职责。模块可以根据需要选择和组合所需的配置。如果某个模块有一次性逻辑且没有共享代码，最好直接在该模块的 `build.gradle` 中定义，而不是创建一个包含模块特定配置的约定插件。

当前的约定插件列表：

- [`nowinandroid.android.application`](convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt)、
  [`nowinandroid.android.library`](convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt)、
  [`nowinandroid.android.test`](convention/src/main/kotlin/AndroidTestConventionPlugin.kt)：
  配置通用的 Android 和 Kotlin 选项。
- [`nowinandroid.android.application.compose`](convention/src/main/kotlin/AndroidApplicationComposeConventionPlugin.kt)、
  [`nowinandroid.android.library.compose`](convention/src/main/kotlin/AndroidLibraryComposeConventionPlugin.kt)：
  配置 Jetpack Compose 选项
