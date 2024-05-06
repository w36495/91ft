package com.w36495.senty.view

import android.os.Bundle
import android.view.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.view.screen.FindPasswordDialogScreen
import com.w36495.senty.view.screen.ui.theme.SentyTheme

class ResetPasswordDialog : DialogFragment() {
    private val findPasswordViewModel by viewModels<ResetPasswordViewModel>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        auth = FirebaseAuth.getInstance()

        return ComposeView(requireContext()).apply {
            setContent {
                val hasEmailError by findPasswordViewModel.hasEmailError.collectAsState()
                val emailErrorMsg by findPasswordViewModel.emailErrorMsg.collectAsState()

                SentyTheme {
                    FindPasswordDialogScreen(
                        onPositiveClick = { email ->
                            if (findPasswordViewModel.validateEmail(email)) {
                                sendPasswordResetEmail(email)
                            }
                        },
                        onDismissClick = {
                            dismiss()
                        },
                        hasEmailError = hasEmailError,
                        emailErrorMsg = emailErrorMsg
                    )
                }
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dismiss()
                }
            }
    }
}