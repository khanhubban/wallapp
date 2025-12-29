package wallapp.content.state.error.signin

import kotlinx.coroutines.flow.StateFlow
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewState
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.pixel.view.ViewEventHandler
import wallapp.screen.ScreenArgument
import wallapp.util.combine
import wallapp.view.ViewStateRefresher

class ErrorSignInViewModel(
    private val argument: ScreenArgument.ErrorScreenArgument,
    appStateManager: AppStateManager,
    viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
) : ErrorViewModel(
    appStateManager = appStateManager,
    errorScreen = argument.errorScreen,
    viewStateMapper = viewStateMapper,
    viewStateRefresher = viewStateRefresher,
) {
    private val errorScreen: ErrorScreen.SignIn
        get() = argument.errorScreen as ErrorScreen.SignIn
    private val errorMessage: String?
        get() = errorScreen.errorMessage

    private val onRetryViewEventHandler: ViewEventHandler = ViewEventHandler.createOnClick {
        appStateManager.navigateBack()
    }

    private fun createViewState(isUiReady: Boolean): ErrorViewState {
        if (!isUiReady) {
            return ErrorViewState.Loading
        }
        return viewStateMapper.createSignInErrorViewState(errorMessage, onRetryViewEventHandler)
    }

    override val viewState: StateFlow<ErrorViewState> = combine(
        appStateManager.isUiReady,
        viewStateRefresher.refresh,
    ) { isUiReady, _ ->
        createViewState(isUiReady = isUiReady)
    }.stateIn(createViewState(isUiReady = false))
}