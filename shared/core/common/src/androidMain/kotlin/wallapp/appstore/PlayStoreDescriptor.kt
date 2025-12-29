package wallapp.appstore

import wallapp.phrase.Phrase

object PlayStoreDescriptor : AppStoreDescriptor {

    override val storeApplicationId: String
        get() = "com.android.vending"

    private val storeUrlFormatWithReferrer: String
        get() = "https://play.google.com/store/apps/details?id={app_id}&referrer=utm_source%3Dwallapp%26utm_medium%3Dapp%26utm_campaign%3D{referrer}"
    private val storeUrlFormatWithoutReferrer: String
        get() = "https://play.google.com/store/apps/details?id={app_id}"

    override fun getStoreUrl(applicationId: String, referrer: String?): String {
        val phrase = if (referrer != null) {
            Phrase.from(storeUrlFormatWithReferrer)
                .put("referrer", referrer)
        } else {
            Phrase.from(storeUrlFormatWithoutReferrer)
        }

        return phrase.put("app_id", applicationId)
            .format()
            .toString()
    }
}