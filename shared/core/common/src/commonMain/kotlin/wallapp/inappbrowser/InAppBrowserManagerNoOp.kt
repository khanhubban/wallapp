package wallapp.inappbrowser

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object InAppBrowserManagerNoOp : InAppBrowserManager {

    override val isShowing: StateFlow<Boolean> = MutableStateFlow(false)

    override fun show(url: String): Flow<InAppBrowserEvent> = TODO()
}