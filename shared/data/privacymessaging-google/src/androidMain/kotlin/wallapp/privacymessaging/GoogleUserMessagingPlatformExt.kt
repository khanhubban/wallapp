package wallapp.privacymessaging

import com.google.android.ump.FormError

fun fromErrorCode(errorCode: Int): PrivacyMessagingFormErrorCode {
    return when (errorCode) {
        FormError.ErrorCode.INTERNAL_ERROR -> PrivacyMessagingFormErrorCode.InternalError
        FormError.ErrorCode.INTERNET_ERROR -> PrivacyMessagingFormErrorCode.InternetError
        FormError.ErrorCode.INVALID_OPERATION -> PrivacyMessagingFormErrorCode.InvalidOperation
        FormError.ErrorCode.TIME_OUT -> PrivacyMessagingFormErrorCode.Timeout
        else -> PrivacyMessagingFormErrorCode.Unknown(errorCode)
    }
}

val FormError.toPrivacyMessagingFormError: PrivacyMessagingFormError?
    get() = PrivacyMessagingFormError(
        errorCode = fromErrorCode(this.errorCode),
        errorMessage = this.message,
        nativeObject = this,
    )
