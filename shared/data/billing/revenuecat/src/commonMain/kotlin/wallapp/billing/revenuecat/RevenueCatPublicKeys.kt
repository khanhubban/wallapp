package wallapp.billing.revenuecat

object RevenueCatPublicKeys {

    private val keys = mapOf(
        "com.example.wallpapers.playstore" to "<add_me>",
        "com.example.wallpapers.appstore" to "<add_me>",
        "com.example.wallpapers.appstore.development" to "<add_me>",
    )

    fun get(packageName: String): String? {
        return keys[packageName]
    }
}