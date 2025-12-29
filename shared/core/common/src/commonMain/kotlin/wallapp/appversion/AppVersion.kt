package wallapp.appversion

import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import wallapp.appversion.AppVersion.AppVersionAndroid
import wallapp.appversion.AppVersion.AppVersionIos

@SealedInterop.Enabled
@Serializable(with = AppVersionSerializer::class)
sealed class AppVersion {

    abstract val versionName: String
    // Remove the (x) suffix from the version name on iOS.
    abstract val versionNameUserFacing: String

    val jsonString: String
        get() = AppVersionJson.encodeToString(serializer(), this)

    fun isNewerThan(other: AppVersion): Boolean? {
        return when {
            this is AppVersionAndroid && other is AppVersionAndroid -> isNewerThan(other)
            this is AppVersionIos && other is AppVersionIos -> isNewerThan(other)
            else -> null
        }
    }

    abstract fun isValid(): Boolean

    @Serializable
    @SerialName("AppVersionAndroid")
    data class AppVersionAndroid(
        override val versionName: String,
        val versionCode: Long,
    ) : AppVersion() {
        override val versionNameUserFacing: String
            get() = versionName

        fun isNewerThan(other: AppVersionAndroid): Boolean {
            return versionCode > other.versionCode
        }

        override fun isValid(): Boolean = versionCode > 0
    }

    @Serializable
    @SerialName("AppVersionIos")
    data class AppVersionIos(
        val shortVersion: String,
        val buildNumber: Long,
    ) : AppVersion() {

        override val versionName: String
            get() = "$shortVersion ($buildNumber)"

        override val versionNameUserFacing: String
            get() = shortVersion

        fun isNewerThan(other: AppVersionIos): Boolean? {
            val thisVersionPartsNullable = this.shortVersion.split(".").map { it.toIntOrNull() }
            val otherVersionPartsNullable = other.shortVersion.split(".").map { it.toIntOrNull() }
            if (thisVersionPartsNullable.any { it == null } || otherVersionPartsNullable.any { it == null }) {
                return null
            }

            val thisVersionParts = thisVersionPartsNullable.filterNotNull()
            val otherVersionParts = otherVersionPartsNullable.filterNotNull()

            for (i in thisVersionParts.indices) {
                if (i >= otherVersionParts.size) {
                    return true
                }
                if (thisVersionParts[i] > otherVersionParts[i]) {
                    return true
                }
                if (thisVersionParts[i] < otherVersionParts[i]) {
                    return false
                }
            }

            if (thisVersionParts.size < otherVersionParts.size) {
                return false
            }

            // If versions are the same, compare build number
            return this.buildNumber > other.buildNumber
        }

        override fun isValid(): Boolean {
            return validateIosShortVersion(shortVersion) && buildNumber > 0
        }

    }

    companion object {

        internal val AppVersionJson = Json

        fun fromExportString(exportString: String): AppVersion? {
            if (exportString.isEmpty()) {
                return null
            }
            return try {
                AppVersionJson.decodeFromString<AppVersion?>(serializer(), exportString)
                    .let {
                        if (it?.isValid() == true) it else null
                    }
            } catch (e: SerializationException) {
                null
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        fun validateIosShortVersion(shortVersion: String): Boolean {
            if (shortVersion.isEmpty()) return false

            val items = shortVersion.split(".")
            val numbers = items.mapNotNull { it.toIntOrNull() }
            return numbers.isNotEmpty() && numbers.size == items.size
        }
    }
}

val AppVersion.versionCode: Long?
    get() = when (this) {
        is AppVersionAndroid -> versionCode
        is AppVersionIos -> null
    }