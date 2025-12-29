package wallapp.content.state.error.appupdaterequired

import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.view.ViewStateRefresher

class ErrorAppUpdateRequiredViewModel(
    appStateManager: AppStateManager,
    viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
) : ErrorViewModel(appStateManager, ErrorScreen.AppUpdateRequired, viewStateMapper, viewStateRefresher)
