package wallapp.resources.string

import kotlinx.datetime.Instant

interface StringArbitrator {

    fun storeName(
        appStore: String,
        playStore: String,
        fallback: String,
    ): String

    fun updateAppAction(
        openInAppStore: String,
        openInGooglePlay: String,
    ): String

    fun networkErrorAction(
        openNetworkSettings: String,
        openSettings: String,
    ): String?

    fun date(date: Instant): String

    val versionName: String
    val copyrightYear: String
}