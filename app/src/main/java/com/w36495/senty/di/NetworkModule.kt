package com.w36495.senty.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.w36495.senty.BuildConfig
import com.w36495.senty.data.remote.service.AnniversaryService
import com.w36495.senty.data.remote.service.AuthService
import com.w36495.senty.data.remote.service.FriendGroupService
import com.w36495.senty.data.remote.service.FriendService
import com.w36495.senty.data.remote.service.GiftCategoryService
import com.w36495.senty.data.remote.service.GiftService
import com.w36495.senty.data.remote.service.MapSearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL: String = BuildConfig.DATABASE_BASE_URL
    private const val NAVER_BASE_URL: String = BuildConfig.NAVER_GEOCODING_BASE_URI

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class naver

    @Provides
    @Singleton
    @naver
    fun provideNaverRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(NAVER_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        logger: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggerInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
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
    fun provideAuthApi(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    @naver
    fun provideMapSearchService(@naver retrofit: Retrofit): MapSearchService = retrofit.create(MapSearchService::class.java)
}