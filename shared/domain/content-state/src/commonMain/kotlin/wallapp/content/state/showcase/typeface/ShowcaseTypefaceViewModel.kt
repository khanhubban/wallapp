package wallapp.content.state.showcase.typeface

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.view.ViewStateFactory
import wallapp.viewmodel.ViewModel

class ShowcaseTypefaceViewModel(
    viewStateFactory: ViewStateFactory,
) : ViewModel(), ScreenViewStateProvider {

    val toolbarViewState = viewStateFactory.createToolbar("Typography Showcase")

    @FlowInterop.Enabled
    override val viewState: StateFlow<ShowcaseTypefaceViewState> =
        MutableStateFlow(ShowcaseTypefaceViewState(toolbarViewState))
}