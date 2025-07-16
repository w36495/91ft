package com.w36495.senty.application

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kakao.sdk.common.KakaoSdk
import com.w36495.senty.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class SentyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        AndroidThreeTen.init(this)
        Firebase.initialize(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}