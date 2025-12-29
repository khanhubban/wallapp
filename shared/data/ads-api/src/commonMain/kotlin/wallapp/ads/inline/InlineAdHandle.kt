package wallapp.ads.inline

import kotlinx.coroutines.flow.StateFlow

interface InlineAdHandle {

    val viewStateFlow: StateFlow<InlineAdViewState>
}
