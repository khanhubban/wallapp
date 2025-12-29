package wallapp.content.state.profile

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wallapp.account.Account
import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.account.state.signin.SignInViewModel
import wallapp.app.AppStateManager
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.social.SocialLinkViewEvent
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.ConnectionsSummaryContentResult
import wallapp.graphics.Color
import wallapp.image.Image
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.view.ViewEventSink
import wallapp.profileimage.ProfileImageManager
import wallapp.purchase.PurchaseUiManager
import wallapp.system.navigation.SystemNavigator
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

class ProfileViewModel(
    appStateManager: AppStateManager,
    contentRepository: ContentRepository,
    accountManager: AccountManager,
    private val profileImageManager: ProfileImageManager,
    private val signInViewModel: SignInViewModel,
    private val messageBarManager: MessageBarManager,
    private val viewStateFactory: ViewStateFactory,
    purchaseUiManager: PurchaseUiManager,
    private val contentColorManager: ContentColorManager,
    viewStateRefresher: ViewStateRefresher,
    systemNavigator: SystemNavigator,
    private val accountDataRepository: AccountDataRepository,
) : ViewModel(), ScreenViewStateProvider {

    private val scrollToTop: MutableSharedFlow<Unit> = MutableSharedFlow()
    fun triggerScrollToTop() {
        viewModelScope.launch {
            scrollToTop.emit(Unit)
        }
    }

    private val socialLinkEventSink: ViewEventSink = { event ->
        if (event is SocialLinkViewEvent) {
            systemNavigator.toUrl(event.url)
        }
    }

    private val profileTopBarContainer: StateFlow<Color>
        get() = contentColorManager.profileTopBarContainer


    private val scrollStateController = FeedScrollStateController(viewModelScope)

    private fun createViewState(
        isUiReady: Boolean,
        account: Account? = null,
        profileImage: Image = profileImageManager.profileImage.value,
        showPurchasePlusUi: Boolean = false,
        connectionsSummaryContent: ConnectionsSummaryContentResult? = null,
        messageBar: MessageBarViewState? = null,
        profileTopBarContainer: Color = this.profileTopBarContainer.value,
        showDebugOptions: Boolean = false,
    ): ProfileViewState {
        if (!isUiReady) return ProfileViewState.Loading
        return viewStateFactory.createProfileViewState(
            account = account,
            profileImage = profileImage,
            messageBar = messageBar,
            appleSignInOnClick = signInViewModel.appleSignInButtonViewState?.viewEventHandler,
            googleSignInOnClick = signInViewModel.googleSignInButtonViewState.viewEventHandler,
            showPurchasePlusUi = showPurchasePlusUi,
            profileTopBarContainerColor = profileTopBarContainer,
            connectionsSummaryContent = connectionsSummaryContent,
            socialLinkEventSink = socialLinkEventSink,
            scrollToTop = scrollToTop,
            scrollStateWrapper = scrollStateController.scrollStateWrapper,
            showDebugOptions = showDebugOptions,
        )
    }

    override val viewState: StateFlow<ProfileViewState> by lazy {
        combine(
            appStateManager.isUiReady,
            accountManager.signedInAccount,
            profileImageManager.profileImage,
            purchaseUiManager.showInAccount,
            contentRepository.connectionsSummaryContent,
            messageBarManager.messageBarViewState,
            profileTopBarContainer,
            accountDataRepository.hasSpecialCaseIsDeveloper,
            viewStateRefresher.refresh,
            scrollStateController.lastScrollStateUpdateFlow,
        ) { isUiReady, signedInAccount, profileImage, showPurchaseUi, connectionsSummaryContent, messageBar, profileTopBarContainer, showDebugOptions, _, _ ->
            createViewState(isUiReady, signedInAccount, profileImage, showPurchaseUi, connectionsSummaryContent, messageBar, profileTopBarContainer, showDebugOptions == true)
        }.stateIn(createViewState(isUiReady = false))
    }
}
