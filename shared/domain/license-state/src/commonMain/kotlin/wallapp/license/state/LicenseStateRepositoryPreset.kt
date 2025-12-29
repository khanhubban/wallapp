package wallapp.license.state

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.licensing.CheckLicenseStateResult
import wallapp.licensing.LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
import wallapp.licensing.LICENSE_STATE_NOT_APPLICABLE
import wallapp.licensing.LicenseInfo

class LicenseStateRepositoryPreset(
    initialLicenseInfo: LicenseInfo,
): LicenseStateRepository {

    override val licenseInfo: MutableStateFlow<LicenseInfo> = MutableStateFlow(initialLicenseInfo)

    override val isLicensed: MutableStateFlow<Boolean> = MutableStateFlow(
        initialLicenseInfo.licenseState == LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
                || initialLicenseInfo.licenseState == LICENSE_STATE_NOT_APPLICABLE
    )

    override fun checkLicenseState(forceUpdate: Boolean): CheckLicenseStateResult {
        return CheckLicenseStateResult.CHECKING
    }

    override fun setLicenseInfoState(licenseInfo: LicenseInfo) { }
}
