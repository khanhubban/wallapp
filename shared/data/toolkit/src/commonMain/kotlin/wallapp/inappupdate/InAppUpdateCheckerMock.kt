package wallapp.inappupdate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("MemberVisibilityCanBePrivate", "PropertyName")
class InAppUpdateCheckerMock(appUpdateRequired: Boolean = false) : InAppUpdateChecker {

    val _appUpdateRequired = MutableStateFlow(appUpdateRequired)
    override val appUpdateRequired: StateFlow<Boolean>
        get() = _appUpdateRequired
}