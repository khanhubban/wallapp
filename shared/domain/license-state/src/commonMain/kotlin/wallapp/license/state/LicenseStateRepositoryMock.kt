package wallapp.license.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.licensing.CheckLicenseStateResult
import wallapp.licensing.LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
import wallapp.licensing.LicenseInfo
import wallapp.licensing.isLicenseStateAllowedAny


open class LicenseStateRepositoryMock(
    var mockLicenceInfo: LicenseInfo = LicenseInfo(LICENSE_STATE_ALLOWED_PLUS_UNLIMITED),
): LicenseStateRepository {

    private val _licenseInfo = MutableStateFlow(mockLicenceInfo)
    override val licenseInfo: StateFlow<LicenseInfo>
        get() = _licenseInfo

    private val _isLicensed = MutableStateFlow<Boolean>(isLicenseStateAllowedAny(mockLicenceInfo.licenseState))
    override val isLicensed: StateFlow<Boolean> get() = _isLicensed

    override fun checkLicenseState(forceUpdate: Boolean): CheckLicenseStateResult {
        _licenseInfo.value = mockLicenceInfo
        _isLicensed.value  = mockLicenceInfo.licenseState == LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
        return CheckLicenseStateResult.CHECKING
    }

    override fun setLicenseInfoState(licenseInfo: LicenseInfo) {
        _licenseInfo.value = licenseInfo
        mockLicenceInfo = licenseInfo
        updateLicenseInfoState()
    }

    init {
        updateLicenseInfoState()
    }

    private fun updateLicenseInfoState() {
        _isLicensed.value = isLicenseStateAllowedAny(mockLicenceInfo.licenseState)
    }
}
