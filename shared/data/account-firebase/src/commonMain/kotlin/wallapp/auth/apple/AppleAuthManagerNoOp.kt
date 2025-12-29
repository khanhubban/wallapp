package wallapp.auth.apple

import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseUser
import wallapp.result.ResultEx

object AppleAuthManagerNoOp : AppleAuthManager {

    override fun appleOAuthCredential(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<AuthCredential> {
        return ResultEx.Error(Exception("AppleAuthManagerNoOp"))
    }

    override suspend fun updateDisplayName(firebaseUser: FirebaseUser, appleAuthSignInResult: AppleAuthSignInResult): Boolean {
        return false
    }

    override suspend fun linkWithCredential(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<String> {
        return ResultEx.Error(Exception("AppleAuthManagerNoOp"))
    }

    override suspend fun revokeToken(appleAuthSignInResult: AppleAuthSignInResult): ResultEx<Unit> {
        return ResultEx.Error(Exception("AppleAuthManagerNoOp"))
    }
}