package wallapp.license.state

sealed class LicenseStateType {
    data object Plus : LicenseStateType()
    data object AdFree : LicenseStateType()
    data object Unlicensed : LicenseStateType()
}

fun LicenseStateType.isLicensedAny(): Boolean = 
    when (this) {
        LicenseStateType.Plus -> true
        LicenseStateType.AdFree -> true
        LicenseStateType.Unlicensed -> false
    }

fun LicenseStateType.isUnlicensed(): Boolean =
    this is LicenseStateType.Unlicensed

fun LicenseStateType.isPlus(): Boolean =
    this is LicenseStateType.Plus