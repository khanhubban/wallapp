package wallapp.pixel.screen

import kotlinx.coroutines.flow.StateFlow

interface ScreenViewStateProvider {

    val viewState: StateFlow<ScreenViewState>
}