package wallapp.content.state.error

import kotlinx.coroutines.flow.StateFlow
import wallapp.app.AppStateManager
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.util.combine
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

open class ErrorViewModel(
    protected val appStateManager: AppStateManager,
    private val errorScreen: ErrorScreen,
    protected val viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), ScreenViewStateProvider {

    protected val isUiReady: StateFlow<Boolean>
        get() = appStateManager.isUiReady

    private fun createViewState(isUiReady: Boolean): ErrorViewState {
        if (!isUiReady) {
            return ErrorViewState.Loading
        }
        return viewStateMapper.createViewState(errorScreen)
    }

    override val viewState: StateFlow<ErrorViewState> by lazy {
        combine(
            isUiReady,
            viewStateRefresher.refresh,
        ) { isUiReady, _ ->
            createViewState(isUiReady = isUiReady)
        }.stateIn(createViewState(isUiReady = false))
    }
}

