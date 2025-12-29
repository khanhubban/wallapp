package wallapp.messaging

import wallapp.account.AccountManager
import wallapp.log.Logger

abstract class CloudMessagingManagerDefault(
    private val accountManager: AccountManager,
) : CloudMessagingManager {

    val Log = Logger("[FCM] CloudMessagingManager")

    override fun onNewToken(token: String) {
        Log.d("Refreshed token: $token")
        accountManager.updateFirebaseCloudMessagingToken(token)
    }

}