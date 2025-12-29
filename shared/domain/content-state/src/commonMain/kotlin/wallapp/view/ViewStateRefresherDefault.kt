package wallapp.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import wallapp.account.data.AccountDataRepository
import wallapp.ad.AdManager
import wallapp.appconfig.AppConfig
import wallapp.coroutine.CoroutineScopeIo
import wallapp.license.state.LicenseState
import wallapp.preferences.UserPreferences
import wallapp.random.RandomManager
import wallapp.system.window.WindowFrameManager
import wallapp.theme.ThemeManager
import wallapp.util.combine

class ViewStateRefresherDefault(
    adManager: AdManager,
    themeManager: ThemeManager,
    accountDataRepository: AccountDataRepository,
    userPreferences: UserPreferences,
    appConfig: AppConfig,
    windowFrameManager: WindowFrameManager,
    licenseState: LicenseState,
    private val randomManager: RandomManager,
    @CoroutineScopeIo private val scopeIo: CoroutineScope,
) : ViewStateRefresher {

    private val manualRefreshState = MutableStateFlow(1)

    private var refreshState = 1111
    override val refresh: Flow<Int> by lazy {
        combine(
            licenseState.licenseStateType,
            adManager.feedAdsEnabled,
            themeManager.theme,
            themeManager.themeType,
            userPreferences.appIcon,
            appConfig.staggeredFeed,
            accountDataRepository.favoriteIds,
            windowFrameManager.windowFrame,
            manualRefreshState,
            randomManager.randomSeed,
        ) { _, _ , _ , _, _, _, _, _, _, _ ->
            ++refreshState
        }.stateIn(
            scopeIo,
            started = SharingStarted.WhileSubscribed(),
            initialValue = refreshState,
        )
    }

    fun forceRefresh() {
        manualRefreshState.value = manualRefreshState.value + 1
    }

    override fun globalRefresh() {
        randomManager.refresh()
    }

    override fun onCleared() { }
}