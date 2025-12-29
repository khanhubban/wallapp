package wallapp.licensing


data class LicenseInfo(val licenseState: Int) {
    val debugString: String
        get() = licenseStateToString(licenseState)
}