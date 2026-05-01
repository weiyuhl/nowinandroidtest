package com.lhzkml.nowtest.lint.designsystem

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.lhzkml.nowtest.lint.designsystem.DesignSystemDetector.Companion.ISSUE
import com.lhzkml.nowtest.lint.designsystem.DesignSystemDetector.Companion.METHOD_NAMES
import com.lhzkml.nowtest.lint.designsystem.DesignSystemDetector.Companion.RECEIVER_NAMES
import org.junit.Test

class DesignSystemDetectorTest {

    @Test
    fun `detect replacements of Composable`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                COMPOSABLE_STUB,
                STUBS,
                @Suppress("LintImplTrimIndent")
                kotlin(
                    """
                    |import androidx.compose.runtime.Composable
                    |
                    |@Composable
                    |fun App() {
                    ${METHOD_NAMES.keys.joinToString("\n") { "|    $it()" }}
                    |}
                    """.trimMargin(),
                ).indented(),
            )
            .run()
            .expect(
                """
                src/test.kt:5: Error: Using MaterialTheme instead of NtTheme [DesignSystem]
                    MaterialTheme()
                    ~~~~~~~~~~~~~~~
                src/test.kt:6: Error: Using Button instead of NtButton [DesignSystem]
                    Button()
                    ~~~~~~~~
                src/test.kt:7: Error: Using OutlinedButton instead of NtOutlinedButton [DesignSystem]
                    OutlinedButton()
                    ~~~~~~~~~~~~~~~~
                src/test.kt:8: Error: Using TextButton instead of NtTextButton [DesignSystem]
                    TextButton()
                    ~~~~~~~~~~~~
                src/test.kt:9: Error: Using FilterChip instead of NtFilterChip [DesignSystem]
                    FilterChip()
                    ~~~~~~~~~~~~
                src/test.kt:10: Error: Using ElevatedFilterChip instead of NtFilterChip [DesignSystem]
                    ElevatedFilterChip()
                    ~~~~~~~~~~~~~~~~~~~~
                src/test.kt:11: Error: Using NavigationBar instead of NtNavigationBar [DesignSystem]
                    NavigationBar()
                    ~~~~~~~~~~~~~~~
                src/test.kt:12: Error: Using NavigationBarItem instead of NtNavigationBarItem [DesignSystem]
                    NavigationBarItem()
                    ~~~~~~~~~~~~~~~~~~~
                src/test.kt:13: Error: Using NavigationRail instead of NtNavigationRail [DesignSystem]
                    NavigationRail()
                    ~~~~~~~~~~~~~~~~
                src/test.kt:14: Error: Using NavigationRailItem instead of NtNavigationRailItem [DesignSystem]
                    NavigationRailItem()
                    ~~~~~~~~~~~~~~~~~~~~
                src/test.kt:15: Error: Using TabRow instead of NtTabRow [DesignSystem]
                    TabRow()
                    ~~~~~~~~
                src/test.kt:16: Error: Using Tab instead of NtTab [DesignSystem]
                    Tab()
                    ~~~~~
                src/test.kt:17: Error: Using IconToggleButton instead of NtIconToggleButton [DesignSystem]
                    IconToggleButton()
                    ~~~~~~~~~~~~~~~~~~
                src/test.kt:18: Error: Using FilledIconToggleButton instead of NtIconToggleButton [DesignSystem]
                    FilledIconToggleButton()
                    ~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:19: Error: Using FilledTonalIconToggleButton instead of NtIconToggleButton [DesignSystem]
                    FilledTonalIconToggleButton()
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:20: Error: Using OutlinedIconToggleButton instead of NtIconToggleButton [DesignSystem]
                    OutlinedIconToggleButton()
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:21: Error: Using CenterAlignedTopAppBar instead of NtTopNavigationBar [DesignSystem]
                    CenterAlignedTopAppBar()
                    ~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:22: Error: Using SmallTopAppBar instead of NtTopNavigationBar [DesignSystem]
                    SmallTopAppBar()
                    ~~~~~~~~~~~~~~~~
                src/test.kt:23: Error: Using MediumTopAppBar instead of NtTopNavigationBar [DesignSystem]
                    MediumTopAppBar()
                    ~~~~~~~~~~~~~~~~~
                src/test.kt:24: Error: Using LargeTopAppBar instead of NtTopNavigationBar [DesignSystem]
                    LargeTopAppBar()
                    ~~~~~~~~~~~~~~~~
                20 errors, 0 warnings
                """.trimIndent(),
            )
    }

    @Test
    fun `detect replacements of Receiver`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                COMPOSABLE_STUB,
                STUBS,
                @Suppress("LintImplTrimIndent")
                kotlin(
                    """
                    |fun main() {
                    ${RECEIVER_NAMES.keys.joinToString("\n") { "|    $it.toString()" }}
                    |}
                    """.trimMargin(),
                ).indented(),
            )
            .run()
            .expect(
                """
                src/test.kt:2: Error: Using Icons instead of NtIcons [DesignSystem]
                    Icons.toString()
                    ~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent(),
            )
    }

    private companion object {

        private val COMPOSABLE_STUB: TestFile = kotlin(
            """
            package androidx.compose.runtime
            annotation class Composable
            """.trimIndent(),
        ).indented()

        private val STUBS: TestFile = kotlin(
            """
            |import androidx.compose.runtime.Composable
            |
            ${METHOD_NAMES.keys.joinToString("\n") { "|@Composable fun $it() = {}" }}
            ${RECEIVER_NAMES.keys.joinToString("\n") { "|object $it" }}
            |
            """.trimMargin(),
        ).indented()
    }
}
