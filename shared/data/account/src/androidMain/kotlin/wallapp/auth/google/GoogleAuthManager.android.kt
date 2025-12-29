package wallapp.auth.google

import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.tasks.await
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.log.Log
import wallapp.network.NetworkConnectionException
import wallapp.result.ResultEx
import wallapp.system.intent.intent
import java.util.concurrent.ExecutionException


class GoogleAuthManagerAndroid(
    private val context: Context,
    private val signInClient: GoogleSignInClient,
) : GoogleAuthManager {

    private var account: GoogleSignInAccount? = null
    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    override suspend fun isSignedIn(): Boolean {
        if (getAccount() == null) return false

        try {
            Log.d("[firebase] Try to silently sign in to check if user revoked app's access")
            account = signInClient.silentSignIn().await()
            return true
        } catch (ex: ExecutionException) {
            Log.w(ex, ex.localizedMessage)
            val cause = ex.cause
            if (cause is ApiException && cause.statusCode == CommonStatusCodes.SIGN_IN_REQUIRED) {
                return false
            }
            throw NetworkConnectionException(cause = ex)
        }
    }

    override fun onNewSignIn(result: GoogleAuthSignInResult): ResultEx<Unit> {
        val intent = result.intentWrapper.intent
        Log.d("[firebase] Getting account from intent")
        account = try {
            GoogleSignIn
                .getSignedInAccountFromIntent(intent)
                .getResult(ApiException::class.java)
        } catch (ex: ApiException) {
            crashTracking.logNonFatalException(ex)
            Log.e(ex, "signInResult:failed code=" + ex.statusCode)
            return ResultEx.Error(Exception("Google sign in failed, code=${ex.statusCode}", ex))
    //            throw RuntimeException(GOOGLE_ACCOUNT_ERROR)
        }
        return if (account != null) {
            Log.d("[firebase] Google Signed in")
            ResultEx.Success(Unit)
        } else {
            Log.e("Google account is null")
            ResultEx.Error(Exception(GOOGLE_ACCOUNT_ERROR))
        }
    }

    override suspend fun signOut() {
        try {
            signInClient.signOut().await()
            account = null
            Log.d("[firestore] Google Signed out")
        } catch (ex: Exception) {
            Log.w(ex, ex.localizedMessage)
            throw NetworkConnectionException(cause = ex)
        }
    }

    override suspend fun revokeAppAccess() {
        try {
            signInClient.revokeAccess().await()
            Log.d("[firebase] Signed out")
        } catch (ex: Exception) {
            Log.w(ex, ex.localizedMessage)
            throw NetworkConnectionException(cause = ex)
        }
    }

    override fun getGoogleAuthData(): GoogleAuthData? {
        return getAccount()?.let { account ->
            val idToken = account.idToken
            GoogleAuthData(
                idToken = idToken,
                accessToken = null,
                name = account.displayName,
                photoUrl = account.photoUrl?.toString(),
            )
        }
    }

//    override fun getSignedInAccountEmail(): String? {
//        return getAccount()?.email
//    }

    override fun clearToken(token: String) {
        GoogleAuthUtil.clearToken(context, token)
    }

    private fun getAccount(): GoogleSignInAccount? {
        if (account == null) {
            account = GoogleSignIn.getLastSignedInAccount(context)
            Log.d("[firebase] GoogleSignIn Account retrieved - %b", account != null)
        }
        return account
    }
}
