package wallapp.flavorconfig

abstract class FlavorConfig {

    abstract val applicationId: String

    abstract val exposeUserAccount: Boolean

    abstract val cachedLicenseState: String

    abstract val requireLicense: Boolean

    open val supportsInstantApps: Boolean
        get() = false

    open val debugLicenseState: Int
        get() = 456 // LICENSE_STATE_ALLOWED

    open val useCompose: Boolean
        get() = false
}