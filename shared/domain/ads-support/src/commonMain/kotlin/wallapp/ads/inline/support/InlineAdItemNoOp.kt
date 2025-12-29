package wallapp.ads.inline.support

import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.inline.InlineAdViewState
import wallapp.system.ui.controller.UiController

object InlineAdItemNoOp : InlineAdItem {
    override val viewStateFlow: StateFlow<InlineAdViewState>
        get() = throw NotImplementedError("Should not be used/called")

    override fun bindAd(parent: Any, delayInit: Boolean) {
        throw NotImplementedError("Should not be used/called")
    }

    override fun bind(uiController: UiController, delayInit: Boolean) {
        throw NotImplementedError("Should not be used/called")
    }
}