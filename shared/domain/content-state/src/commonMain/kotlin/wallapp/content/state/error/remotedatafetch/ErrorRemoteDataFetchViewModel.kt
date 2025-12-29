package wallapp.content.state.error.remotedatafetch

import kotlinx.coroutines.flow.StateFlow
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewState
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.coroutine.collectIn
import wallapp.network.NetworkRefreshManager
import wallapp.util.combine
import wallapp.view.ViewEventFactory
import wallapp.view.ViewStateRefresher

class ErrorRemoteDataFetchViewModel(
    appStateManager: AppStateManager,
    networkRefreshManager: NetworkRefreshManager,
    viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
    viewEventFactory: ViewEventFactory,
) : ErrorViewModel(
    appStateManager = appStateManager,
    errorScreen = ErrorScreen.RemoteDataFetch,
    viewStateMapper = viewStateMapper,
    viewStateRefresher = viewStateRefresher,
) {

    private var retryClicked = false

    private var retryViewEventHandler = viewEventFactory.createRetryNetworkFetch {
        retryClicked = true
    }

    private fun createViewState(isUiReady: Boolean): ErrorViewState {
        if (!isUiReady) {
            return ErrorViewState.Loading
        }
        return viewStateMapper.createRemoteDataFetchErrorViewState(retryViewEventHandler)
    }

    override val viewState: StateFlow<ErrorViewState> = combine(
        appStateManager.isUiReady,
        viewStateRefresher.refresh,
    ) { isUiReady, _ ->
        createViewState(isUiReady = isUiReady)
    }.stateIn(createViewState(isUiReady = false))

    init {
        combine(isUiReady, networkRefreshManager.allDataLoaded) { isUiReady, allDataLoaded ->
            isUiReady && allDataLoaded
        }.collectIn(viewModelScope) {
            if (it && !retryClicked) {
                appStateManager.navigateBack()
            }
        }
    }
}