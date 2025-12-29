package wallapp.buildconfig

import wallapp.appversion.AppVersion

/**
 *
 */
interface BuildConfig {
    val debug: Boolean

    val appVersion: AppVersion

    val packageName: String

    val appName: String
}
