package wallapp.privacymessaging

import co.touchlab.skie.configuration.annotations.SealedInterop

@SealedInterop.Enabled
sealed class PrivacyMessagingFormErrorCode {

    data object InternalError : PrivacyMessagingFormErrorCode()
    data object InternetError : PrivacyMessagingFormErrorCode()
    data object InvalidOperation : PrivacyMessagingFormErrorCode()
    data object Timeout : PrivacyMessagingFormErrorCode()

    data class Unknown(val errorCode: Int) : PrivacyMessagingFormErrorCode()
}