package wallapp.ads.inline.support

import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.inline.InlineAdViewState
import wallapp.system.ui.controller.UiController


interface InlineAdItem {

    val viewStateFlow: StateFlow<InlineAdViewState>

    fun bindAd(parent: Any /*ViewGroup*/, delayInit: Boolean)

    fun bind(uiController: UiController, delayInit: Boolean)
}
