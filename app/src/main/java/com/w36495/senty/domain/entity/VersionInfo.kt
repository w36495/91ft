package com.w36495.senty.domain.entity

import com.w36495.senty.BuildConfig

data class VersionInfo(
    val currentVersion: String = BuildConfig.VERSION_NAME,
    val latestVersion: String = BuildConfig.VERSION_NAME,
    val isNeedUpdate: Boolean = false,
)