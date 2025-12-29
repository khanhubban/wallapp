package wallapp.licensing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.time.TimeRepository
import wallapp.time.TimeRepositoryMock


class LicenseRepositoryStub(
    initialLicenseInfo: LicenseInfo = LicenseInfo(LICENSE_STATE_ALLOWED_PLUS_UNLIMITED),
    val timeRepository: TimeRepository = TimeRepositoryMock()
): LicenseRepository {

    private var lastLicenseCheckTime = 0L

    private val _licenseInfo = MutableStateFlow(initialLicenseInfo)
    override val licenseInfo: StateFlow<LicenseInfo>
        get() = _licenseInfo

    var pendingLicenseInfo: LicenseInfo? = null

    override fun checkLicenseState(forceUpdate: Boolean): CheckLicenseStateResult {
        val timeSinceLastLicenseCheck = timeRepository.currentTime - lastLicenseCheckTime
        if (!forceUpdate && timeSinceLastLicenseCheck < LICENSE_CHECK_TIME_GAP) {
            return CheckLicenseStateResult.NOT_CHECKING
        }
        lastLicenseCheckTime = timeRepository.currentTime

        pendingLicenseInfo?.let {
            _licenseInfo.value = it
            pendingLicenseInfo = null
        }
        return CheckLicenseStateResult.CHECKING
    }

    override fun setLicenseInfoState(licenseInfo: LicenseInfo) {
        _licenseInfo.value = licenseInfo
    }

    companion object {
        private const val LICENSE_CHECK_TIME_GAP = 10 * 1000 // 10 seconds
    }
}