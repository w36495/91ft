package com.w36495.senty.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.firebase.ui.auth.AuthUI
import com.w36495.senty.data.domain.AccountPreference
import com.w36495.senty.domain.repository.AccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val authUi: AuthUI,
    @ApplicationContext private val context: Context
): AccountRepository {
    private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = "AccountPreferences")

    override fun userLogout(): Boolean {
        var result = false
        authUi.signOut(context)
            .addOnCompleteListener {
                if (it.isSuccessful) result = true
            }

        return result
    }

    override fun deleteUser(): Boolean {
        var result = false

        authUi.delete(context)
            .addOnCompleteListener {
                if (it.isSuccessful) result = true
            }

        return result
    }

    override suspend fun setUserIdPreference(id: String, password: String) {
        context.accountDataStore.edit { preferences ->
            preferences[PREFERENCE_KEY_USER_ID] = id
            preferences[PREFERENCE_KEY_USER_PASSWORD] = password
        }
    }

    override suspend fun getUserIdPreference(): Flow<AccountPreference> =
        context.accountDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("Error reading preferencees.", exception.message.toString())
                    emit(emptyPreferences())
                } else throw exception
            }
            .map { preference ->
                mappingUserIdPreference(preference)
            }

    private fun mappingUserIdPreference(preferences: Preferences): AccountPreference {
        val userId = preferences[PREFERENCE_KEY_USER_ID] ?: PREFERENCE_DEFAULT
        val userPassword = preferences[PREFERENCE_KEY_USER_PASSWORD] ?: PREFERENCE_DEFAULT

        return AccountPreference(userId, userPassword)
    }

    override suspend fun clearUserIdPreference() {
        context.accountDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val PREFERENCE_KEY_USER_ID = stringPreferencesKey("userId")
        private val PREFERENCE_KEY_USER_PASSWORD = stringPreferencesKey("userPassword")
        const val PREFERENCE_DEFAULT = "NOTHING"
    }
}