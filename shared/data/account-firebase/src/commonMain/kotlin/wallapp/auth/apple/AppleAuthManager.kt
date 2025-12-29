package wallapp.auth.apple

import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseUser
import wallapp.result.ResultEx
import wallapp.signin.FirebaseAccountAlreadyLinkedException

interface AppleAuthManager {

    fun appleOAuthCredential(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<AuthCredential>

    suspend fun updateDisplayName(firebaseUser: FirebaseUser, appleAuthSignInResult: AppleAuthSignInResult): Boolean

    /**
     * Uses native Firebase Auth to link Apple credentials to an existing Firebase Auth user.
     * If successful, returns the user's ID.
     * If the Apple credentials are already linked to another Firebase Auth user, returns
     * [FirebaseAccountAlreadyLinkedException].
     */
    suspend fun linkWithCredential(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<String>

    suspend fun revokeToken(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<Unit>
}

expect fun provideAppleAuthManager(): AppleAuthManager