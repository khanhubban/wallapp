package wallapp.content.state.error.permissionphotosandroid

import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.view.ViewStateRefresher


class ErrorPermissionSystemMediaDeniedAndroidViewModel(
    appStateManager: AppStateManager,
    viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
) : ErrorViewModel(
    appStateManager = appStateManager,
    errorScreen = ErrorScreen.PermissionSystemMediaDeniedAndroid,
    viewStateMapper = viewStateMapper,
    viewStateRefresher = viewStateRefresher,
)