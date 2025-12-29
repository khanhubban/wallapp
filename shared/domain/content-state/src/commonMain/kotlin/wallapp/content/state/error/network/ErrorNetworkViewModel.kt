package wallapp.content.state.error.network

import kotlinx.coroutines.flow.StateFlow
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewState
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.coroutine.collectIn
import wallapp.network.NetworkConnectionState
import wallapp.network.NetworkState
import wallapp.network.isConnected
import wallapp.screen.ScreenArgument
import wallapp.util.combine
import wallapp.view.ViewStateRefresher

class ErrorNetworkViewModel(
    private val argument: ScreenArgument.ErrorScreenArgument,
    appStateManager: AppStateManager,
    networkState: NetworkState,
    viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
) : ErrorViewModel(
    appStateManager = appStateManager,
    errorScreen = argument.errorScreen,
    viewStateMapper = viewStateMapper,
    viewStateRefresher = viewStateRefresher,
) {

    private val errorScreen: ErrorScreen.Network
        get() = argument.errorScreen as ErrorScreen.Network
    private val showCloseButton: Boolean
        get() = errorScreen.showCloseButton

    private fun createViewState(isUiReady: Boolean, networkConnectionState: NetworkConnectionState): ErrorViewState {
        if (!isUiReady) {
            return ErrorViewState.Loading
        }
        return viewStateMapper.createNetworkErrorViewState(showCloseButton, networkConnectionState)
    }

    override val viewState: StateFlow<ErrorViewState> = combine(
        appStateManager.isUiReady,
        networkState.networkConnectionState,
        viewStateRefresher.refresh,
    ) { isUiReady, networkConnectionState, _ ->
        createViewState(isUiReady = isUiReady, networkConnectionState = networkConnectionState)
    }.stateIn(createViewState(isUiReady = false, networkConnectionState = NetworkConnectionState.Unknown))

    init {
        combine(isUiReady, networkState.networkConnectionState) { isUiReady, connectionState ->
            // Require UI to be ready and network connection to be connected before auto-exiting
            isUiReady && connectionState.isConnected
        }
            .collectIn(viewModelScope) {
                if (it) {
                    appStateManager.navigateBack()
                }
            }
    }
}