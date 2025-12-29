package wallapp.flavorconfig

import wallapp.device.DeviceId
import wallapp.licensing.LICENSE_STATE_UNKNOWN


class FlavorConfigDefault(
    override val applicationId: String,
    private val deviceId: DeviceId,
) : FlavorConfig() {

    override val exposeUserAccount: Boolean
        get() = true
    override val cachedLicenseState: String
        get() = "${deviceId.bestDeviceId}.$LICENSE_STATE_UNKNOWN"
    override val requireLicense: Boolean
        get() = false
    override val useCompose: Boolean
        get() = true
}