package wallapp.license.controller

import wallapp.licensing.CheckLicenseStateResult


class LicenseCheckControllerNoOp : LicenseCheckController {

    override fun checkLicenseIfRequired() { }

    override fun checkLicenseState(): CheckLicenseStateResult {
        return CheckLicenseStateResult.NOT_CHECKING
    }
}