package wallapp.license.controller

import wallapp.licensing.CheckLicenseStateResult

interface LicenseCheckController {

    fun checkLicenseIfRequired()

    fun checkLicenseState(): CheckLicenseStateResult
}