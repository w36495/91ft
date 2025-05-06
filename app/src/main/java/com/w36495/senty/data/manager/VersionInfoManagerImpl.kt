package com.w36495.senty.data.manager

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.w36495.senty.BuildConfig
import com.w36495.senty.domain.entity.VersionInfo
import com.w36495.senty.domain.manager.VersionInfoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class VersionInfoManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig,
) : VersionInfoManager {
    private val _versionInfo = MutableStateFlow(VersionInfo())
    override val versionInfo: StateFlow<VersionInfo>
        get() = _versionInfo.asStateFlow()

    init {
        initConfig()
        loadLatestVersionInfo()
    }

    override fun checkUpdate() {
        try {
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val remoteVersion = remoteConfig.getString(KEY_LATEST_VERSION)
                        val currentVersion = getPackageVersionInfo()?.first

                        currentVersion?.let { packageVersion ->
                            val (remoteMajor, remoteMinor, _) = remoteVersion.split(".")
                            val (packageMajor, packageMinor, _) = packageVersion.split(".")

                            val isSameMajor = remoteMajor == packageMajor
                            val isSameMinor = remoteMinor == packageMinor

                            _versionInfo.update {
                                VersionInfo(
                                    latestVersion = remoteVersion,
                                    currentVersion = packageVersion,
                                    isNeedUpdate = !isSameMajor || !isSameMinor,
                                )
                            }
                        }
                    } else {
                        _versionInfo.update { VersionInfo(isNeedUpdate = false) }
                    }
                }
        } catch (e: Exception) {
            _versionInfo.update { VersionInfo(isNeedUpdate = false) }
        }
    }

    private fun initConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) MINIMUM_FETCH_DEBUG else MINIMUM_FETCH_RELEASE
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mutableMapOf<String, String>().apply {
            put("latestVersion", getPackageVersionInfo()?.first ?: BuildConfig.VERSION_NAME)
        } as Map<String, Any>).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("VersionInfoProvider", "기본 값 설정 완료")
            }
        }
    }

    private fun loadLatestVersionInfo() {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d("VersionInfoProvider", "최신 버전 업데이트 완료")

                if (configUpdate.updatedKeys.contains(KEY_LATEST_VERSION)) {
                    remoteConfig.activate().addOnCompleteListener {
                        checkUpdate()
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.d("VersionInfoProvider", error.stackTraceToString())
            }
        })
    }

    private fun getPackageVersionInfo(): Pair<String, Long>? {
        return try {
            val packageInfo = context.applicationContext
                .packageManager
                .getPackageInfo(PACKAGE_NAME, 0)

            val version = packageInfo.versionName

            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else packageInfo.versionCode.toLong()

            Log.d("VersionInfoProvider", "$version / $versionCode")

            version?.let { Pair(it, versionCode) }
        } catch (e: Exception) {
            Log.d("VersionInfoProvider", e.stackTraceToString())
            null
        }
    }

    companion object {
        private const val PACKAGE_NAME = "com.w36495.senty"
        private const val KEY_LATEST_VERSION = "latestVersion"
        private const val MINIMUM_FETCH_DEBUG: Long = 1
        private const val MINIMUM_FETCH_RELEASE: Long = 3_600
    }
}