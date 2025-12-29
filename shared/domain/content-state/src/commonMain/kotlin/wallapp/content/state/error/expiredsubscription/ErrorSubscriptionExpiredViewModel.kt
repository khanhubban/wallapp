package wallapp.content.state.error.expiredsubscription

import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.error.ErrorViewModel
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.coroutine.collectIn
import wallapp.entitlement.EntitlementRepository
import wallapp.license.state.isLicensedAny
import wallapp.view.ViewStateRefresher

class ErrorSubscriptionExpiredViewModel(
    appStateManager: AppStateManager,
    viewStateMapper: ErrorViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
    entitlementRepository: EntitlementRepository,
) : ErrorViewModel(
    appStateManager = appStateManager,
    errorScreen = ErrorScreen.SubscriptionExpired,
    viewStateMapper = viewStateMapper,
    viewStateRefresher = viewStateRefresher,
) {
    init {
        entitlementRepository.licenseState.collectIn(viewModelScope) { licenseState ->
            if (licenseState.isLicensedAny()) {
                appStateManager.navigateBack()
            }
        }
    }
}
