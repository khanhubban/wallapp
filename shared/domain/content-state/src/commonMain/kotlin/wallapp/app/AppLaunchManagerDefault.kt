package wallapp.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import wallapp.content.model.Id
import wallapp.content.state.index.IndexTab
import wallapp.coroutine.collectIn
import wallapp.data.content.ContentRepository
import wallapp.deeplink.DeepLinkManager
import wallapp.screen.ScreenArgument
import wallapp.util.combine

class AppLaunchManagerDefault(
    private val deepLinkManager: DeepLinkManager,
    private val appStateManager: AppStateManager,
    contentRepository: ContentRepository,
    private val coroutineScopeMain: CoroutineScope,
) : AppLaunchManager {

    private var hasRun: Boolean = false

    override fun run() {
        if (hasRun) return
        hasRun = true

//        navigateToDeferred { navigateToScreen(ScreenArgument.BannerExportScreenArgument) }
//        navigateToDeferred { appStateManager.navigateToError(ErrorScreen.SignIn(errorMessage = "Account Error: Server sign in:\nAn internal error has occurred. [ Read error:ssl=0xb40000772c9d3a08: I/O error during system call, Software caused connection abort ]")) }
//        navigateToDeferred { navigateToScreen(RewardAdInternalScreenArgument(wallpaperId = WallpaperId(name = "a~artistname_d4215991")) ) }
//        navigateToDeferred { navigateToScreen(FolderScreenArgument(Id.FolderId("f~justadded"))) }
//        navigateToDeferred { navigateToScreen(WallpaperShowcaseScreenArgument(remixId = RemixId(name = "a~artistname_d4215991"))) }
//        navigateToScreenDeferred(ArtistId("a~artistname"))
//        navigateToScreenDeferred(CategoryId("indigo~blue~stripes"))
//        navigateToDeferred { navigateToScreen(WallpaperShowcaseScreenArgument(remixId = RemixId(name = "a~artistname_a84e29a4"))) }
//        navigateToDeferred { navigateToScreen(WallpaperShowcaseScreenArgument(remixId = RemixId(name = "a~artistname_0e732a6f"))) }
//        navigateToDeferred { navigateToScreen(ScreenArgument.CollectionIdScreenArgument(CollectionId("artistname~dunes"), firstWallpaperId = null)) }
//        navigateToDeferred { appStateManager.navigateToScreen(BuyCollectionScreenArgument(CollectionId("artistname~dunes"), triggerStorePurchaseOnOpen = false )) }
//        navigateToDeferred { appStateManager.navigateToScreen(ScreenArgument.UnlockWallpaperScreenArgument(Id.RemixId(name = "a~artistname_0e732a6f"), autoPlayRewardAd = false)) }
//        navigateToDeferred { appStateManager.navigateToUpgradeThanksCollection(CollectionId("artistname~dunes")) }
//        navigateToDeferred { appStateManager.navigateToUpgradeThanksPlus() }
//        navigateToDeferred { appStateManager.navigateToPaywall() }
//        navigateToDeferred { appStateManager.navigateToSearch() }
//        navigateToDeferred { appStateManager.navigateToDebugSettings() }
//        navigateToDeferred { appStateManager.navigateToError(ErrorScreen.AppUpdateRequired) }
//        navigateToDeferred { navigateToScreen(ShowcaseAdsScreenArgument) }
//        navigateToDeferred { navigateToScreen(ShowcaseIosNativeFeedScreenArgument) }
//        navigateToIndexTab(IndexTab.Home)
//        navigateToDeferred {
//            navigateToScreen(
//                ScreenArgument.ErrorScreenArgument(
//                    ErrorScreen.RewardAd(
//                        wallpaperId = RemixId(name = "a~artistname_0e732a6f"),
//                        errorMessage = "Error msg",
//                    ),
//                ),
//            )
//        }
    }

    private fun navigateToIndexTab(indexTab: IndexTab) {
        coroutineScopeMain.launch {
            appStateManager.navigateToIndexTab(indexTab)
        }
    }

    private fun navigateToScreen(arguments: ScreenArgument) {
        coroutineScopeMain.launch {
            appStateManager.navigateToScreen(arguments)
        }
    }

    private fun navigateToScreenDeferred(id: Id) {
        navigateToDeferred {
            appStateManager.navigateToScreen(id)
        }
    }

    private fun navigateToDeferred(block: () -> Unit) {
        deferredNavigationBlock.value = block
    }

    private val deferredNavigationBlock: MutableStateFlow<(() -> Unit)?> = MutableStateFlow(null)
    private val navigationBlock: Flow<(() -> Unit)?> = combine(
        contentRepository.isReady,
        deferredNavigationBlock,
    ) { isReady, block ->
        if (isReady) block else null
    }

    init {
        deepLinkManager.navigationArgument.collectIn(coroutineScopeMain) { screenArgument ->
            screenArgument?.also {
                navigateToDeferred {
                    appStateManager.navigateToScreen(screenArgument as ScreenArgument)
                }
            }
        }

        navigationBlock.collectIn(coroutineScopeMain) { navigationBlock ->
            navigationBlock?.also { navigationBlock() }
        }
    }
}