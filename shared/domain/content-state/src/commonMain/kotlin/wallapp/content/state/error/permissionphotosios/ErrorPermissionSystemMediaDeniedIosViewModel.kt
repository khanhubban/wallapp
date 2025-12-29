package wallapp.content.state.error.permissionphotosios

import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.view.ViewStateRefresher


class ErrorPermissionSystemMediaDeniedIosViewModel(
    appStateManager: AppStateManager,
    viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
) : ErrorViewModel(
    appStateManager = appStateManager,
    errorScreen = ErrorScreen.PermissionSystemMediaDeniedIos,
    viewStateMapper = viewStateMapper,
    viewStateRefresher = viewStateRefresher,
)