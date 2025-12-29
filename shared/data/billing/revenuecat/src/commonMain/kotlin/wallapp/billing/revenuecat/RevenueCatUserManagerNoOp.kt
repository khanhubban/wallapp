package wallapp.billing.revenuecat

object RevenueCatUserManagerNoOp : RevenueCatUserManager {
    override fun configure(projectApiKey: String, enableSuperwall: Boolean, userId: String?) {
        // no-op
    }

    override fun login(userId: String, completion: (RevenueCatErrors?) -> Unit) {
        // no-op
    }

    override fun logout(completion: (RevenueCatErrors?) -> Unit) {
        // no-op
    }

    override fun addCurrentAppUserIdListener(listener: CurrentAppUserIdListener) {
        // no-op
    }

    override fun removeCurrentAppUserIdListener(listener: CurrentAppUserIdListener) {
        // no-op
    }

    override fun isCurrentUserAnonymous(): Boolean {
        return false
    }
}