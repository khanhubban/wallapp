package wallapp.auth.apple

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRAuthErrorUserInfoUpdatedCredentialKey
import cocoapods.FirebaseAuth.FIROAuthCredential
import cocoapods.FirebaseAuth.FIROAuthProvider
import cocoapods.FirebaseAuth.FIRUser
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSPersonNameComponents
import wallapp.log.Log
import wallapp.result.ResultEx
import wallapp.signin.FirebaseAccountAlreadyLinkedException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
class AppleAuthManagerIos : AppleAuthManager {

    // When linking Apple credential to an existing Firebase Auth user, the native Firebase Auth
    // library will return an error if the Apple credential are already linked to another Firebase
    // Auth user. In this case, we store the updated credential and return. These credential can
    // then be used to sign in normally without linking.
    // See https://firebase.google.com/docs/auth/ios/apple#sign_in_with_apple_and_authenticate_with_firebase:~:text=Note%20that%20Apple,the%20AuthErrorUserInfoUpdatedCredentialKey%20key.
    // and https://github.com/firebase/firebase-ios-sdk/issues/4434#issuecomment-564776016
    private val credentialFromLinkCredentialError = mutableMapOf<String, FIROAuthCredential>()

    private val currentUser: FIRUser?
        get() = FIRAuth.auth().currentUser()

    override fun appleOAuthCredential(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<AuthCredential> {
        return if (appleAuthSignInResult.success) {
            Log.d("[firebase] appleOAuthCredential(), name: ${appleAuthSignInResult.fullName?.givenName}, ${appleAuthSignInResult.fullName?.familyName}")

            // If linking credential had failed, the next sign-in event will be in continuation of
            // the linking event, so we return the stored credential.
            val credentialFromLinkingError = appleAuthSignInResult.rawNonce?.let {
                credentialFromLinkCredentialError[appleAuthSignInResult.rawNonce]
            }
            if (credentialFromLinkingError != null) {
                credentialFromLinkCredentialError.remove(appleAuthSignInResult.rawNonce)
                return ResultEx.Success(AuthCredential(credentialFromLinkingError))
            }

            ResultEx.Success(AuthCredential(appleAuthSignInResult.appleCredential()))
        } else {
            ResultEx.Error(Exception(appleAuthSignInResult.error?.localizedDescription))
        }
    }

    override suspend fun updateDisplayName(firebaseUser: FirebaseUser, appleAuthSignInResult: AppleAuthSignInResult): Boolean {
        val displayName = currentUser?.displayName()
        if (displayName?.isNotBlank() == true) {
            Log.d("[firebase] displayName already set: ${displayName}")
            return true
        }
        try {
            firebaseUser.updateProfile(displayName = displayName(appleAuthSignInResult.fullName))
        } catch (e: Exception) {
            Log.w("[firebase] Failed to update displayName: ${e.message}")
            return false
        }
        return true
    }

    override suspend fun linkWithCredential(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<String> {
        val credential = if (appleAuthSignInResult.success) {
            appleAuthSignInResult.appleCredential()
        } else {
            return ResultEx.Error(Exception(appleAuthSignInResult.error?.localizedDescription))
        }
        val currentUser = currentUser ?: return ResultEx.Error(Exception("No currentUser"))

        return suspendCoroutine {
            currentUser.linkWithCredential(credential) { authResult, error ->
                when {
                    authResult != null -> {
                        it.resume(ResultEx.Success(authResult.user().uid()))
                    }
                    error != null -> {
                        val updatedCredential = error.userInfo[FIRAuthErrorUserInfoUpdatedCredentialKey] as? FIROAuthCredential
                        if (updatedCredential != null) {
                            credentialFromLinkCredentialError[appleAuthSignInResult.rawNonce!!] = updatedCredential
                            it.resume(ResultEx.Error(FirebaseAccountAlreadyLinkedException))
                        } else {
                            it.resume(ResultEx.Error(Exception("Error linking with credential: ${error.localizedDescription}")))
                        }
                    }
                    else -> it.resume(ResultEx.Error(Exception("Error linking with credential: unknown error")))
                }
            }
        }
    }

    override suspend fun revokeToken(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<Unit> {
        val authorizationCodeString = appleAuthSignInResult.authorizationCodeString
        if (authorizationCodeString == null) {
            Log.e("[firebase] No authorizationCodeString to revoke")
            return ResultEx.Error(Exception("No authorizationCodeString to revoke"))
        }
        return suspendCoroutine {
            FIRAuth.auth().revokeTokenWithAuthorizationCode(authorizationCodeString) { error ->
                if (error != null) {
                    it.resume(ResultEx.Error(Exception("Error revoking token: ${error.localizedDescription}")))
                } else {
                    Log.d("[firebase] Token revoked")
                    it.resume(ResultEx.Success(Unit))
                }
            }
        }
    }

    private fun AppleAuthSignInResult.appleCredential() =
        FIROAuthProvider.appleCredentialWithIDToken(
            idToken = idToken!!,
            rawNonce = rawNonce,
            fullName = fullName,
        )

    private fun displayName(fullName: NSPersonNameComponents?): String {
        if (fullName == null) {
            Log.d("[firebase] displayName(): null")
            return ""
        }
        return listOfNotNull(fullName.givenName, fullName.familyName)
            .joinToString(separator = " ")
            .takeIf { it.isNotBlank() }
            ?.also { displayName ->
                Log.d("[firebase] displayName(): $displayName")
            } ?: ""
    }
}