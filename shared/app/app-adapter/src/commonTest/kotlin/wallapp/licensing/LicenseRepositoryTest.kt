package wallapp.licensing

import kotlin.test.Test
import kotlin.test.assertEquals

class LicenseRepositoryTest {

    @Test
    fun `should not allow successive license checks when forceUpdate is false`() {
        val licenseRepository = LicenseRepositoryStub(initialLicenseInfo = LicenseInfo(
            LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
        ))
        licenseRepository.checkLicenseState(forceUpdate = false)
        assertEquals(LICENSE_STATE_ALLOWED_PLUS_UNLIMITED, licenseRepository.licenseInfo.value?.licenseState)

        licenseRepository.pendingLicenseInfo = LicenseInfo(LICENSE_STATE_NOT_ALLOWED)

        // license info should not change
        licenseRepository.checkLicenseState(forceUpdate = false)
        assertEquals(LICENSE_STATE_ALLOWED_PLUS_UNLIMITED, licenseRepository.licenseInfo.value?.licenseState)
    }

    @Test
    fun `should allow successive license checks when forceUpdate is true`() {
        val licenseRepository = LicenseRepositoryStub(initialLicenseInfo = LicenseInfo(
            LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
        ))
        licenseRepository.checkLicenseState(forceUpdate = true)
        assertEquals(LICENSE_STATE_ALLOWED_PLUS_UNLIMITED, licenseRepository.licenseInfo.value?.licenseState)

        licenseRepository.pendingLicenseInfo = LicenseInfo(LICENSE_STATE_NOT_ALLOWED)

        licenseRepository.checkLicenseState(forceUpdate = true)
        assertEquals(LICENSE_STATE_NOT_ALLOWED, licenseRepository.licenseInfo.value?.licenseState)
    }
}