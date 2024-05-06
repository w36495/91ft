package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.R
import com.w36495.senty.view.screen.LoginScreen
import com.w36495.senty.view.screen.ui.theme.SentyTheme

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            val hasEmailError by loginViewModel.hasEmailError.collectAsState()
            val emailErrorMsg by loginViewModel.emailErrorMsg.collectAsState()
            val hasPasswordError by loginViewModel.hasPasswordError.collectAsState()
            val passwordErrorMsg by loginViewModel.passwordErrorMsg.collectAsState()

            SentyTheme {
                LoginScreen(
                    hasEmailError = hasEmailError,
                    hasPasswordError = hasPasswordError,
                    emailErrorMsg = emailErrorMsg,
                    passwordErrorMsg = passwordErrorMsg,
                    onBackPressed = { onBackPressed() },
                    onClickLogin = { email, password ->
                        login(email, password)
                    },
                    onFindPassword = {
                        ResetPasswordDialog().show(supportFragmentManager, "resetPasswdDialog")
                    }
                )
            }
        }
    }

    // firebase 로그인
    private fun login(email: String, password: String) {
        if (!loginViewModel.isValid(email, password)) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, FriendListActivity::class.java)
                    finishAffinity()
                    startActivity(intent)
//                    val userSharedPref = getSharedPreferences("user", Context.MODE_PRIVATE)
//                    with(userSharedPref.edit()) {
//                        putString("userId", FirebaseAuth.getInstance().currentUser!!.uid)
//                        commit()
//                    }
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_login_exception),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}