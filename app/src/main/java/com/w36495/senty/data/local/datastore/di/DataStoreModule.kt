package com.w36495.senty.data.local.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.w36495.senty.domain.local.datastore.DataStoreContact
import com.w36495.senty.data.local.datastore.FriendSyncFlagDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("91ft_dataStore_pref")
        }
    }

    @Provides
    @Singleton
    fun provideFriendSyncFlagDataStore(
        dataStore: DataStore<Preferences>
    ): DataStoreContact<Boolean> {
        return FriendSyncFlagDataStore(dataStore)
    }
}