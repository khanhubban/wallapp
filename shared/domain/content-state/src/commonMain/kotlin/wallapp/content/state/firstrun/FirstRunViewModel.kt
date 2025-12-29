package wallapp.content.state.firstrun

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.account.state.signin.SignInViewModel
import wallapp.app.AppStateManager
import wallapp.content.model.Wallpaper
import wallapp.data.content.ContentCacheManager
import wallapp.data.content.ContentRepository
import wallapp.entitlement.EntitlementRepository
import wallapp.license.state.isLicensedAny
import wallapp.onboarding.OnboardingState
import wallapp.permission.SystemPermissionManager
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.view.ViewEventHandler
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.system.navigation.SystemNavigator
import wallapp.util.combine
import wallapp.view.ViewEventFactory
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateMapper
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

class FirstRunViewModel(
    appStateManager: AppStateManager,
    contentRepository: ContentRepository,
    private val viewStateFactory: ViewStateFactory,
    private val viewStateMapper: ViewStateMapper,
    private val signInViewModel: SignInViewModel,
    private val accountManager: AccountManager,
    private val accountDataRepository: AccountDataRepository,
    entitlementRepository: EntitlementRepository,
    private val contentCacheManager: ContentCacheManager,
    private val viewEventFactory: ViewEventFactory,
    viewStateRefresher: ViewStateRefresher,
    private val systemPermissionManager: SystemPermissionManager,
    private val systemNavigator: SystemNavigator,
) : ViewModel(), ScreenViewStateProvider, ScreenSystemBarControllerHolder {

    companion object {
        const val ShimmerLoopDuration = 1350L
    }

    private val showcaseWallpapers: Flow<List<Wallpaper>?> =
        contentRepository.signUpContentResult
            .map { signUpContentResult ->
                signUpContentResult.showcaseWallpapers
            }

    private val List<Wallpaper>.carouselImages: List<ImageViewState>
        get() {
            return if (this.isEmpty()) {
                emptyList()
            } else {
                viewStateMapper.mapFirstRunCarouselBackgrounds(this)
            }
        }

    private val currentBackgroundIndex = MutableStateFlow(0)

    private val signUpFinished = MutableStateFlow(false)
    private val currentScreenType = combine(
        signUpFinished,
        entitlementRepository.licenseState,
        accountManager.signedInAccount,
    ) { signUpFinished, _, signedInAccount ->
        if (signedInAccount != null || signUpFinished) {
            OnboardingState.DataConsent
        } else {
            OnboardingState.SignUp
        }
    }.stateIn(initialValue = OnboardingState.SignUp)

    private val skipOnClick = {
        signUpFinished.value = true
    }

    private val acceptedTerms: MutableStateFlow<Boolean>
        get() = accountDataRepository.acceptedTerms
    private val reportUsageStats: MutableStateFlow<Boolean>
        get() = accountDataRepository.reportUsageStats
    private val subscribedToNewsletter: StateFlow<Boolean>
        get() = accountDataRepository.receiveNewsletter
    private val showAcceptedTermsShimmer: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private fun createViewState(
        isUiReady: Boolean,
        showcaseWallpapers: List<Wallpaper>?,
        currentScreenType: OnboardingState,
        isSubscriber: Boolean,
        isShowSocialButton: Boolean,
    ): FirstRunViewState {
        if (!isUiReady || showcaseWallpapers == null || showcaseWallpapers.isEmpty()) {
            return FirstRunViewState.Loading
        }

        return viewStateFactory.createFirstRunViewState(
            currentScreenType = currentScreenType,
            showcaseBackgroundImages = showcaseWallpapers.carouselImages,
            currentShowcaseIndex = currentBackgroundIndex,
            googleSignInButtonViewState = signInViewModel.googleSignInButtonViewState,
            appleSignInButtonViewState = signInViewModel.appleSignInButtonViewState,
            acceptedTerms = acceptedTerms,
            subscribedToNewsletter = subscribedToNewsletter,
            reportUsageStats = reportUsageStats,
            receiveNotifications = MutableStateFlow(accountDataRepository.receiveNotifications.value),
            showAcceptedTermsShimmer = showAcceptedTermsShimmer.value,
            continueEventHandler = createContinueViewEventHandler(),
            isShowSocialButton = isShowSocialButton,
            showUpgradeButton = !isSubscriber,
            skipOnClick = skipOnClick,
        )
    }

    private fun createContinueViewEventHandler(): ViewEventHandler {
        return if (acceptedTerms.value) {
            showAcceptedTermsShimmer.value = false
            viewEventFactory.createOnboardingFinished()
        } else {
            ViewEventHandler.createOnClick {
                showAcceptedTermsShimmer.value = true
                viewModelScope.launch {
                    delay(ShimmerLoopDuration)
                    showAcceptedTermsShimmer.value = false
                }
            }
        }
    }

    override val viewState: StateFlow<ScreenViewState> = combine(
        appStateManager.isUiReady,
        showcaseWallpapers,
        currentScreenType,
        entitlementRepository.licenseState,
        accountManager.signedInAccount,
        acceptedTerms,
        subscribedToNewsletter,
        reportUsageStats,
        accountDataRepository.receiveNotifications,
        showAcceptedTermsShimmer,
        viewStateRefresher.refresh,
    ) { isUiReady, showcaseWallpapers, currentScreenType, licenseState, signedInAccount, _, _, _, _, _, _ ->
        createViewState(
            isUiReady = isUiReady,
            showcaseWallpapers = showcaseWallpapers,
            currentScreenType = currentScreenType,
            isSubscriber = licenseState.isLicensedAny(),
            isShowSocialButton = signedInAccount == null,
        )
    }.stateIn(
        initialValue = createViewState(
            isUiReady = false,
            showcaseWallpapers = null,
            currentScreenType = OnboardingState.SignUp,
            isSubscriber = false,
            isShowSocialButton = true,
        )
    )

    init {
        viewModelScope.launch {
            appStateManager.waitForUiReady()
            appStateManager.waitForMediaReady()

            contentCacheManager.prefetchHomeOnboarding()
        }
    }

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> =
        MutableStateFlow(ScreenSystemBarController.TranslucentStatusBar())
}
