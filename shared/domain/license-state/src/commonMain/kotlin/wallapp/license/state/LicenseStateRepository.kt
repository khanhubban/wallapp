package wallapp.license.state

import kotlinx.coroutines.flow.StateFlow
import wallapp.licensing.CheckLicenseStateResult
import wallapp.licensing.LicenseInfo

/**
 * As a rule, this class should only be accessed by code responsible for checking/changing
 * the license state.
 *
 * App related code requiring a license state should instead use [wallapp.license.state.LicenseState].
 */
interface LicenseStateRepository {

    @Deprecated("Use licenseInfo.licenseState instead")
    val isLicensed: StateFlow<Boolean>

    val licenseInfo: StateFlow<LicenseInfo>

    fun checkLicenseState(forceUpdate: Boolean): CheckLicenseStateResult

    fun setLicenseInfoState(licenseInfo: LicenseInfo)
}
