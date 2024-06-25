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
import com.w36495.senty.data.domain.UserEntity
import com.w36495.senty.data.remote.service.AuthService
import com.w36495.senty.domain.repository.AccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class AccountRepositoryImpl @Inject constructor(
    private val authUi: AuthUI,
    private val authService: AuthService,
    @ApplicationContext private val context: Context
): AccountRepository {
    private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = "AccountPreferences")

    override suspend fun userLogout(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            authUi.signOut(context)
                .addOnCompleteListener {
                    continuation.resumeWith(Result.success(it.isSuccessful))
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }

            continuation.invokeOnCancellation {
                throw IllegalArgumentException(it?.message.toString())
            }
        }
    }

    override fun deleteUser(): Boolean {
        var result = false

        authUi.delete(context)
            .addOnCompleteListener {
                if (it.isSuccessful) result = true
            }

        return result
    }

    override suspend fun insertUser(uid: String, user: UserEntity): Response<ResponseBody> {
        return authService.insertUser(uid, user)
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
            preferences.remove(PREFERENCE_KEY_USER_ID)
            preferences.remove(PREFERENCE_KEY_USER_PASSWORD)
        }
    }

    override suspend fun hasSavedUserIdPreference(): Flow<Boolean> {
        return context.accountDataStore.data.map {
            it.contains(PREFERENCE_KEY_USER_ID)
        }
    }

    companion object {
        private val PREFERENCE_KEY_USER_ID = stringPreferencesKey("userId")
        private val PREFERENCE_KEY_USER_PASSWORD = stringPreferencesKey("userPassword")
        const val PREFERENCE_DEFAULT = "NOTHING"
    }
}