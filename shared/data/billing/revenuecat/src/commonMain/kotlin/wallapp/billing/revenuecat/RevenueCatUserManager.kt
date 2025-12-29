package wallapp.billing.revenuecat

interface RevenueCatUserManager {
    fun configure(projectApiKey: String, enableSuperwall: Boolean, userId: String? = null)
    fun login(userId: String, completion: (RevenueCatErrors?) -> Unit)
    fun logout(completion: (RevenueCatErrors?) -> Unit)
    fun addCurrentAppUserIdListener(listener: CurrentAppUserIdListener)
    fun removeCurrentAppUserIdListener(listener: CurrentAppUserIdListener)
    fun isCurrentUserAnonymous(): Boolean
}

interface CurrentAppUserIdListener {
    fun onCurrentAppUserIdChanged(appUserId: String)
}

data class RevenueCatErrors(
    val code: Int,
    val description: String,
)