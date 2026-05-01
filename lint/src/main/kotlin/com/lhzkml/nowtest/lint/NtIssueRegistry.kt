package com.lhzkml.nowtest.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.lhzkml.nowtest.lint.designsystem.DesignSystemDetector

class NtIssueRegistry : IssueRegistry() {

    override val issues = listOf(
        DesignSystemDetector.ISSUE,
        TestMethodNameDetector.FORMAT,
        TestMethodNameDetector.PREFIX,
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "nowtest",
        feedbackUrl = "https://github.com/weiyuhl/nowtest/issues",
        contact = "https://github.com/weiyuhl/nowtest",
    )
}
