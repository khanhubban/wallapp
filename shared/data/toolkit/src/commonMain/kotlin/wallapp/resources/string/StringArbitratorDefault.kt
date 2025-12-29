package wallapp.resources.string

import kotlinx.datetime.Instant
import wallapp.appversion.versionCode
import wallapp.buildconfig.BuildConfig
import wallapp.time.TimeRepository
import kotlin.math.max

class StringArbitratorDefault(
    private val buildConfig: BuildConfig,
    private val dateTimeFormatter: DateTimeFormatter,
    private val timeRepository: TimeRepository,
    private val platform: StringArbitratorPlatform = StringArbitratorPlatform.arbitrated(),
) : StringArbitrator {

    override fun storeName(appStore: String, playStore: String, fallback: String): String {
        return when (platform) {
            StringArbitratorPlatform.Android -> playStore
            StringArbitratorPlatform.Ios -> appStore
            else -> fallback
        }
    }

    override fun updateAppAction(
        openInAppStore: String,
        openInGooglePlay: String,
    ): String {
        return when (platform) {
            StringArbitratorPlatform.Android -> openInGooglePlay
            StringArbitratorPlatform.Ios -> openInAppStore
            else -> openInAppStore
        }
    }

    override fun networkErrorAction(
        openNetworkSettings: String,
        openSettings: String,
    ): String? {
        return when (platform) {
            StringArbitratorPlatform.Android -> openNetworkSettings
            StringArbitratorPlatform.Ios -> openSettings
            else -> null
        }
    }

    override fun date(date: Instant): String {
        return dateTimeFormatter.getLocalizedDate(date)
    }

    override val versionName: String
        get() = if (buildConfig.debug) {
            buildConfig.appVersion.versionName + " (debug) ${buildConfig.appVersion.versionCode?.let { "($it)" } ?: ""}"
        } else {
            buildConfig.appVersion.versionNameUserFacing
        }

    override val copyrightYear: String
        get() = max(timeRepository.currentYear, 2024).toString()
}