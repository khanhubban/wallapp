package wallapp.messaging

import wallapp.account.AccountManager

class CloudMessagingManagerIos(
    accountManager: AccountManager,
) : CloudMessagingManagerDefault(accountManager) {

    override fun initialize() {
        // Not needed for iOS
    }
}