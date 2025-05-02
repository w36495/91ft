package com.w36495.senty.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.w36495.senty.domain.local.datastore.DataStoreContact
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FriendSyncFlagDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : DataStoreContact<Boolean> {
    companion object {
        private val KEY_FRIEND_SYNC_FLAG = booleanPreferencesKey("key_friend_sync_flag")
    }

    override suspend fun save(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_FRIEND_SYNC_FLAG] = value
        }
    }

    override suspend fun load(): Boolean? {
        return dataStore.data
            .map { it[KEY_FRIEND_SYNC_FLAG] }
            .firstOrNull()
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_FRIEND_SYNC_FLAG)
        }
    }
}