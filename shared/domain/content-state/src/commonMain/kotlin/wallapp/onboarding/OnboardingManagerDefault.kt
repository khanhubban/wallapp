package wallapp.onboarding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import wallapp.account.Account
import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.app.AppStateManager
import wallapp.appstate.AppState
import wallapp.onboarding.OnboardingManagerArbitrator.arbitrateOnboardingState
import wallapp.util.combine

class OnboardingManagerDefault(
    val appState: AppState,
    val appStateManager: AppStateManager,
    val accountManager: AccountManager,
    val accountDataRepository: AccountDataRepository,
    coroutineScopeMain: CoroutineScope,
): OnboardingManager {

    private val firstRunOnboardingDismissed: MutableStateFlow<Boolean>
        get() = appState.firstRunOnboardingDismissed
    private val currentAccount: StateFlow<Account?>
        get() = accountManager.signedInAccount
    private val acceptedTerms: StateFlow<Boolean>
        get() = accountDataRepository.acceptedTerms
    private val subscribedToNewsletter: StateFlow<Boolean>
        get() = accountDataRepository.receiveNewsletter
    private val reportUsageStats: StateFlow<Boolean>
        get() = accountDataRepository.reportUsageStats

    override val targetOnboardingState: StateFlow<OnboardingState?> =
        combine(
            firstRunOnboardingDismissed,
            currentAccount,
            acceptedTerms,
            subscribedToNewsletter,
            reportUsageStats,
        ) {
          firstRunOnboardingDismissed,
          currentAccount,
          acceptedTerms,
          subscribedToNewsletter,
          reportUsageStats ->
            arbitrateOnboardingState(
                firstRunOnboardingDismissed = firstRunOnboardingDismissed,
                hasCurrentAccount = currentAccount != null,
                acceptedTerms = acceptedTerms,
                subscribedToNewsletter = subscribedToNewsletter,
                reportUsageStats = reportUsageStats,
            )
        }.stateIn(coroutineScopeMain, started = SharingStarted.WhileSubscribed(), initialValue = null)

    override fun setOnboardingFinished(value: Boolean) {
        appState.firstRunOnboardingDismissed.value = value
    }

    override val homeOnboardingFinished: MutableStateFlow<Boolean>
        get() = appState.homeOnboardingDismissed

    override val artistFollowOnboardingCount: Int
        get() = 2

    override fun setHomeOnboardingFinished(value: Boolean) {
        appState.homeOnboardingDismissed.value = value
    }
}