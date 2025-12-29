package wallapp.license.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LicenseStateSessionManagerNoOp : LicenseStateSessionManager {

    override val granted: StateFlow<Boolean> = MutableStateFlow(false)

    override fun onDeepLinkUrl(url: String) = Unit
}