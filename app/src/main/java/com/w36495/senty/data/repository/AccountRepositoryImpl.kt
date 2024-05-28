package com.w36495.senty.data.repository

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.w36495.senty.domain.repository.AccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val authUi: AuthUI,
    @ApplicationContext private val context: Context
): AccountRepository {

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
}