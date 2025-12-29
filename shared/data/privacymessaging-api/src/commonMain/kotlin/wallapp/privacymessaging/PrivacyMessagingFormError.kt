package wallapp.privacymessaging

data class PrivacyMessagingFormError(
    val errorCode: PrivacyMessagingFormErrorCode,
    val errorMessage: String,
    val nativeObject: Any?,
)
