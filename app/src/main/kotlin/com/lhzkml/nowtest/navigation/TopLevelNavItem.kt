package com.lhzkml.nowtest.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.lhzkml.nowtest.core.designsystem.icon.NtIcons
import com.lhzkml.nowtest.route.test1.contract.navigation.Test1Route
import com.lhzkml.nowtest.route.test2.contract.navigation.Test2Route
import com.lhzkml.nowtest.route.test3.contract.navigation.Test3Route
import com.lhzkml.nowtest.route.test1.contract.R as test1R
import com.lhzkml.nowtest.route.test2.contract.R as test2R
import com.lhzkml.nowtest.route.test3.contract.R as test3R

/**
 * Type for the top level navigation items in the application. Contains UI information about the
 * current route that is used in the navigation UI.
 *
 * @param selectedIcon The icon to be displayed in the navigation UI when this destination is
 * selected.
 * @param unselectedIcon The icon to be displayed in the navigation UI when this destination is
 * not selected.
 * @param iconTextId Text that to be displayed in the navigation UI.
 */
data class TopLevelNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @param:StringRes val iconTextId: Int,
)

val TEST_1 = TopLevelNavItem(
    selectedIcon = NtIcons.Upcoming,
    unselectedIcon = NtIcons.UpcomingBorder,
    iconTextId = test1R.string.route_test1_contract_title,
)

val TEST_2 = TopLevelNavItem(
    selectedIcon = NtIcons.Bookmark,
    unselectedIcon = NtIcons.BookmarkBorder,
    iconTextId = test2R.string.route_test2_contract_title,
)

val TEST_3 = TopLevelNavItem(
    selectedIcon = NtIcons.Grid3x3,
    unselectedIcon = NtIcons.Grid3x3,
    iconTextId = test3R.string.route_test3_contract_title,
)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    Test1Route to TEST_1,
    Test2Route to TEST_2,
    Test3Route to TEST_3,
)
