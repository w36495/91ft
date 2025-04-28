package com.w36495.senty.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.w36495.senty.BuildConfig
import com.w36495.senty.data.remote.service.AccountService
import com.w36495.senty.data.remote.service.AnniversaryService
import com.w36495.senty.data.remote.service.FriendGroupService
import com.w36495.senty.data.remote.service.FriendService
import com.w36495.senty.data.remote.service.GiftCategoryService
import com.w36495.senty.data.remote.service.GiftService
import com.w36495.senty.data.remote.service.MapSearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val ACCOUNT_URL = BuildConfig.FIREBASE_ACCOUNT_BASE_URL
    private const val DATABASE_URL = BuildConfig.FIREBASE_DATABASE_BASE_URL
    private const val NAVER_BASE_URL = BuildConfig.NAVER_GEOCODING_BASE_URI

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class naver

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Account

    private val jsonOptions = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    @naver
    fun provideNaverRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(NAVER_BASE_URL)
        .addConverterFactory(jsonOptions.asConverterFactory("application/json; charset=UTF8".toMediaType()))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(jsonOptions.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .baseUrl(DATABASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Account
    fun provideAccountRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(jsonOptions.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .baseUrl(ACCOUNT_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        logger: HttpLoggingInterceptor,
        @ApplicationContext context: Context,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
//            .addInterceptor(NetworkConnectionInterceptor(context))

    }

    @Provides
    @Singleton
    fun provideLoggerInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideFriendGroupApi(retrofit: Retrofit): FriendGroupService = retrofit.create(FriendGroupService::class.java)

    @Provides
    @Singleton
    fun provideFriendApi(retrofit: Retrofit): FriendService = retrofit.create(FriendService::class.java)

    @Provides
    @Singleton
    fun provideGiftApi(retrofit: Retrofit): GiftService = retrofit.create(GiftService::class.java)

    @Provides
    @Singleton
    fun provideGiftCategoryApi(retrofit: Retrofit): GiftCategoryService = retrofit.create(GiftCategoryService::class.java)

    @Provides
    @Singleton
    fun provideAnniversaryApi(retrofit: Retrofit): AnniversaryService = retrofit.create(AnniversaryService::class.java)

    @Provides
    @Singleton
    @Account
    fun provideAccountApi(@Account retrofit: Retrofit): AccountService = retrofit.create(AccountService::class.java)


    @Provides
    @Singleton
    @naver
    fun provideMapSearchService(@naver retrofit: Retrofit): MapSearchService = retrofit.create(MapSearchService::class.java)
}