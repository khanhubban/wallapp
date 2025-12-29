package wallapp.content.state.dataconsent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import wallapp.account.data.AccountDataRepository
import wallapp.content.model.WallpaperItem
import wallapp.data.content.ContentRepository
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.view.ViewEventHandler
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.view.ViewStateFactory
import wallapp.viewmodel.ViewModel

class DataConsentViewModel(
    private val contentRepository: ContentRepository,
    private val viewStateFactory: ViewStateFactory,
    private val accountDataRepository: AccountDataRepository,
) : ViewModel(), ScreenViewStateProvider, ScreenSystemBarControllerHolder {

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        MutableStateFlow(ScreenSystemBarController.TranslucentStatusBar())
    }

    private val acceptedTerms: MutableStateFlow<Boolean>
        get() = accountDataRepository.acceptedTerms
    private val reportUsageStats: MutableStateFlow<Boolean>
        get() = accountDataRepository.reportUsageStats
    private val subscribedToNewsletter: StateFlow<Boolean>
        get() = accountDataRepository.receiveNewsletter

    private fun createViewState(
        showcaseItems: List<WallpaperItem>? = allShowcaseItems.value,
    ): DataConsentViewState {
        return viewStateFactory.createDataConsentViewState(
            acceptedTerms = acceptedTerms,
            subscribedToNewsletter = subscribedToNewsletter,
            reportUsageStats = reportUsageStats,
            receiveNotifications = MutableStateFlow(false),
            showAcceptedTermsShimmer = false,
            continueEventHandler = ViewEventHandler.NoOp,
        )
    }

    private val allShowcaseItems: StateFlow<List<WallpaperItem>?> =
        contentRepository.signUpContentResult.map {
            it.showcaseWallpapers
        }.stateIn(null)

    override val viewState: StateFlow<DataConsentViewState> =
        combine(
            allShowcaseItems,
            acceptedTerms,
            subscribedToNewsletter,
            reportUsageStats,
        ) { showcaseItems, _, _, _ ->
            createViewState(showcaseItems = showcaseItems)
        }.stateIn(createViewState())
}
