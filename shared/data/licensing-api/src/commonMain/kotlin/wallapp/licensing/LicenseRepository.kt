package wallapp.licensing

import kotlinx.coroutines.flow.StateFlow


interface LicenseRepository {

    val licenseInfo: StateFlow<LicenseInfo>

    fun checkLicenseState(forceUpdate: Boolean): CheckLicenseStateResult

    fun setLicenseInfoState(licenseInfo: LicenseInfo)

}