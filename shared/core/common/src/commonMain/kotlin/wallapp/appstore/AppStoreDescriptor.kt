package wallapp.appstore


interface AppStoreDescriptor {

    val storeApplicationId: String

    fun getStoreUrl(applicationId: String, referrer: String? = null): String
}
