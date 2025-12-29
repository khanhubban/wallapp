package wallapp.messaging

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import wallapp.account.AccountManager

open class CloudMessagingManagerAndroid(
    accountManager: AccountManager,
) : CloudMessagingManagerDefault(accountManager) {

    override fun initialize() {
        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(task.exception, "[FCM] Fetching FCM registration token failed")
                        return@OnCompleteListener
                    }

                    onNewToken(task.result)
                }
            )
    }
}