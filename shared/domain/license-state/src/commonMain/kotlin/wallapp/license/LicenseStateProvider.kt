package wallapp.license


interface LicenseStateProvider {

    val appInstallTime: Long

    val doubleLicenseCheckFinished: Boolean

}


class LicenseStateProviderMock(
    override val appInstallTime: Long = 0,
    override val doubleLicenseCheckFinished: Boolean = false,
) : LicenseStateProvider