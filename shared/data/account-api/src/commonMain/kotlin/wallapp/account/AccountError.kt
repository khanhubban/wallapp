package wallapp.account

import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.string.quote

@SealedInterop.Enabled
sealed class AccountError(
    message: String,
    exception: Exception? = null,
) : Exception(mapMessage(message, exception)) {

    companion object {
        private fun mapMessage(message: String, exception: Exception?): String {
            return "${message}${exception?.message?.let { ":\n$it" } ?: ""}"
        }
    }

    data class AnonymousSignInError(val exception: Exception)
        : AccountError("Account Error: Anonymous sign-in error")
    data class CreateUserProfileError(val exception: Exception?)
        : AccountError("Account Error: Create user profile")
    data class UpdateUserProfileError(val exception: Exception)
        : AccountError("Account Error: Update user profile", exception)
    data class GetUserProfileError(val userId: String)
        : AccountError("Account Error: Get user profile: ${userId.quote()}")
    data class DeleteAccountError(val exception: Exception)
        : AccountError("Account Error: Delete", exception)
    data class FirebaseDeleteUserError(val exception: Exception)
        : AccountError("Account Error: Delete server user")
    data class FirebaseSignInError(val exception: Exception)
        : AccountError("Account Error: Server sign in", exception)
    data object NetworkConnectionError : AccountError("Account Error: Network connection")
    data class ProviderSignInError(val exception: Exception)
        : AccountError("Account Error: Provider sign in", exception)
    data object UserAlreadySignedInError : AccountError("Account Error: Already signed in")
    data object UserNotSignedInError : AccountError("Account Error: Not signed in")
}