package wallapp.license.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.licensing.LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
import wallapp.licensing.LICENSE_STATE_NOT_ALLOWED


abstract class LicenseState {

    abstract val licenseStateType: StateFlow<LicenseStateType>

    /**
     * A LICENSE_STATE_* value. As a rule, aim to use [licenseStateType].
     */
    abstract val state: StateFlow<Int?>

    protected fun mapToState(isLicensed: Boolean, licenseState: Int?): Int? {
        return if (isLicensed) {
            licenseState
        } else {
            LICENSE_STATE_NOT_ALLOWED
        }
    }
}


class LicenseStateNoOp : LicenseState() {

    override val licenseStateType: StateFlow<LicenseStateType> = MutableStateFlow(LicenseStateType.Unlicensed)

    override val state: StateFlow<Int?> = MutableStateFlow(LICENSE_STATE_ALLOWED_PLUS_UNLIMITED)
}


class LicenseStateMock(
    override val licenseStateType: MutableStateFlow<LicenseStateType> = MutableStateFlow(LicenseStateType.Plus),
    override val state: MutableStateFlow<Int?> = MutableStateFlow(LICENSE_STATE_ALLOWED_PLUS_UNLIMITED),
) : LicenseState() {

    constructor(
        isLicensed: Boolean,
        state: Int = LICENSE_STATE_ALLOWED_PLUS_UNLIMITED,
    ) : this(
        MutableStateFlow(if (isLicensed) LicenseStateType.Plus else LicenseStateType.Unlicensed),
        MutableStateFlow(state),
    )

}