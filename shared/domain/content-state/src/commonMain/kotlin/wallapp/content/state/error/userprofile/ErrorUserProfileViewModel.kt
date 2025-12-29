package wallapp.content.state.error.userprofile

import kotlinx.coroutines.flow.StateFlow
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewState
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.screen.ScreenArgument
import wallapp.util.combine
import wallapp.view.ViewStateRefresher

class ErrorUserProfileViewModel (
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
    private val errorScreen: ErrorScreen.UserProfile
        get() = argument.errorScreen as ErrorScreen.UserProfile
    private val errorMessage: String
        get() = errorScreen.errorMessage

    private fun createViewState(isUiReady: Boolean): ErrorViewState {
        if (!isUiReady) {
            return ErrorViewState.Loading
        }
        return viewStateMapper.createUserProfileErrorViewState(errorMessage)
    }

    override val viewState: StateFlow<ErrorViewState> = combine(
        appStateManager.isUiReady,
        viewStateRefresher.refresh,
    ) { isUiReady, _ ->
        createViewState(isUiReady = isUiReady)
    }.stateIn(createViewState(isUiReady = false))
}