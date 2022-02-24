package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.R
import com.w36495.senty.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            val userEmail = binding.signinEmail.editText?.text.toString()
            val userPassword = binding.signinPasswd.editText?.text.toString()
            signIn(userEmail, userPassword)
        }

        // 회원가입 버튼 클릭
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 비밀번호 찾기 버튼 클릭
        binding.signinForgetPasswd.setOnClickListener {
            ResetPasswdDialog().show(supportFragmentManager, "resetPasswdDialog")
        }

        // 뒤로가기 버튼 클릭
        binding.signinToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // firebase 로그인
    private fun signIn(email: String, password: String) {
        if (!isValid(email, password)) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, FriendListActivity::class.java)
                    finishAffinity()
                    startActivity(intent)
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

    private fun isValid(email: String, password: String): Boolean {
        var valid = true
        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_email), Toast.LENGTH_SHORT).show()
            valid = false
        } else if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_password), Toast.LENGTH_SHORT)
                .show()
            valid = false
        }
        return valid
    }

}