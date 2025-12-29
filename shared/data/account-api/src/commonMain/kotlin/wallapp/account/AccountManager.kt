package wallapp.account

import co.touchlab.skie.configuration.annotations.FlowInterop
import co.touchlab.skie.configuration.annotations.SuspendInterop
import kotlinx.coroutines.flow.StateFlow
import wallapp.result.ResultEx

interface AccountManager {

    /**
     * Can be either anonymous or non-anonymous user id.
     */
    val signedInOrAnonymousUserId: StateFlow<String?>

    /**
     * Only contains the signed in user id if non-anonymous user is signed in, null otherwise.
     */
    @FlowInterop.Enabled
    val signedInUserId: StateFlow<String?>

    /**
     * Only contains the signed in account if non-anonymous user is signed in, null otherwise.
     */
    val signedInAccount: StateFlow<Account?>

    @SuspendInterop.Enabled
    suspend fun signIn(signInMethod: SignInMethod): ResultEx<Unit>

    suspend fun signOut(): Boolean

    @SuspendInterop.Enabled
    suspend fun signOutCompletely(): Boolean

    suspend fun deleteAccount(): ResultEx<Unit>

    fun updateFirebaseCloudMessagingToken(token: String)

}

const val RUN_FIREBASE_ON_LOCAL_EMULATORS = false