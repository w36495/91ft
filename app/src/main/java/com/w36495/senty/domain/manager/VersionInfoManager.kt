package com.w36495.senty.domain.manager

import com.w36495.senty.domain.entity.VersionInfo
import kotlinx.coroutines.flow.StateFlow

interface VersionInfoManager {
    val versionInfo: StateFlow<VersionInfo>

    fun checkUpdate()
}