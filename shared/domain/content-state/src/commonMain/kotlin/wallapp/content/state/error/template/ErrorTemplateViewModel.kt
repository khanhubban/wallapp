package wallapp.content.state.error.template

import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.view.ViewStateRefresher

class ErrorTemplateViewModel(
    appStateManager: AppStateManager,
    viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
) : ErrorViewModel(
    appStateManager = appStateManager,
    errorScreen = ErrorScreen.Template,
    viewStateMapper = viewStateMapper,
    viewStateRefresher = viewStateRefresher,
)
