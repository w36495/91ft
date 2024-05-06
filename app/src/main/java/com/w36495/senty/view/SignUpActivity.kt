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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.w36495.senty.R
import com.w36495.senty.view.screen.SignupScreen
import com.w36495.senty.view.screen.ui.theme.SentyTheme

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val signupViewModel by viewModels<SignupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            val emailErrorMsg by signupViewModel.emailErrorMsg.collectAsState()
            val passwordErrorMsg by signupViewModel.passwordErrorMsg.collectAsState()
            val passwordCheckErrorMsg by signupViewModel.passwordCheckErrorMsg.collectAsState()
            val hasEmailError by signupViewModel.hasEmailError.collectAsState()
            val hasPasswordError by signupViewModel.hasPasswordError.collectAsState()
            val hasPasswordCheckError by signupViewModel.hasPasswordCheckError.collectAsState()

            SentyTheme {
                SignupScreen(
                    onBackPressed = {
                        onBackPressed()
                    },
                    onClickSignup = { email, password, passwordCheck ->
                        if (signupViewModel.validateForm(email, password, passwordCheck)) {
                            createAccount(email, password)
                        }
                    },
                    emailErrorMsg = emailErrorMsg,
                    hasEmailError = hasEmailError,
                    passwordErrorMsg = passwordErrorMsg,
                    hasPasswordError = hasPasswordError,
                    passwordCheckErrorMsg = passwordCheckErrorMsg,
                    hasPasswordCheckError = hasPasswordCheckError
                )
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        getString(R.string.msg_complete_signup),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(
                                this,
                                getString(R.string.toast_email_invalid_exception),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(
                                this,
                                getString(R.string.toast_email_collision_exception),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Toast.makeText(
                            this,
                            getString(R.string.msg_failed_signup),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

}