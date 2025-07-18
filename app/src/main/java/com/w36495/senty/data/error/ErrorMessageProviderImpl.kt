package com.w36495.senty.data.error

import com.w36495.senty.R
import com.w36495.senty.domain.error.AuthError
import com.w36495.senty.domain.error.EditGiftError
import com.w36495.senty.domain.error.ErrorHandler
import com.w36495.senty.domain.error.ErrorMessageProvider
import com.w36495.senty.domain.error.GlobalError
import com.w36495.senty.domain.error.ImagePickerError
import javax.inject.Inject

class ErrorMessageProviderImpl @Inject constructor(
    private val errorHandler: ErrorHandler,
) : ErrorMessageProvider {
    override fun getMessageRes(throwable: Throwable?): Int {
        val errorType = errorHandler.handleError(throwable)

        return when (errorType) {
            AuthError.ExistedEmail -> R.string.error_signup_exsist_email
            AuthError.FailedLogin -> R.string.error_login_failed
            AuthError.TooManyRequest -> R.string.error_login_failed_too_many_request
            ImagePickerError.NoGalleryImages -> R.string.error_image_picker_no_gallery_images
            ImagePickerError.NoGalleryFolders -> R.string.error_image_picker_no_gallery_folders
            is EditGiftError -> R.string.error_edit_gift_failed
            GlobalError.NetWork -> R.string.common_error_message_network
            GlobalError.UnKnownError -> R.string.common_error_unknown
        }
    }
}