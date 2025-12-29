package wallapp.auth.google

import platform.Foundation.NSError

actual data class GoogleAuthSignInResult(
    val success: Boolean,
    val error: NSError?,
)
