package wallapp.auth.apple

import platform.Foundation.NSError
import platform.Foundation.NSPersonNameComponents

actual data class AppleAuthSignInResult(
    val success: Boolean,
    val idToken: String?,
    val rawNonce: String?,
    val fullName: NSPersonNameComponents?,
    val authorizationCodeString: String?,
    val error: NSError?
)