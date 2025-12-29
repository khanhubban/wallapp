package wallapp.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.account.data.AccountDataRepository
import wallapp.ads.reward.RewardAdCallbacks
import wallapp.app.AppStateManager
import wallapp.content.model.Id
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.index.IndexTab
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.data.artist.ArtistState
import wallapp.data.favorite.FavoriteItemsRepository
import wallapp.data.following.FollowState
import wallapp.data.following.FollowingRepository
import wallapp.data.following.setFollowing
import wallapp.data.purchase.Purchasable
import wallapp.entitlement.EntitlementRepository
import wallapp.inappreview.InAppReviewRequestManager
import wallapp.license.state.LicenseStateType
import wallapp.network.NetworkRefreshManager
import wallapp.onboarding.OnboardingManager
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionType
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.pixel.globaloverlay.GlobalOverlayViewStateDataScrim
import wallapp.pixel.view.ViewEventHandler
import wallapp.profileimage.ProfileImageManager
import wallapp.purchase.PurchaseManager
import wallapp.screen.ScreenArgument
import wallapp.system.navigation.SystemNavigator

class ViewEventFactoryDefault(
    private val appStateManager: AppStateManager,
    private val favoriteItemsRepository: FavoriteItemsRepository,
    private val followingRepository: FollowingRepository,
    private val onboardingManager: OnboardingManager,
    private val purchaseManager: PurchaseManager,
    private val systemPermissionManager: SystemPermissionManager,
    private val systemNavigator: SystemNavigator,
    private val alertManager: AlertManager,
    private val profileImageManager: ProfileImageManager,
    private val accountDataRepository: AccountDataRepository,
    private val inAppReviewRequestManager: InAppReviewRequestManager,
    private val networkRefreshManager: NetworkRefreshManager,
    private val entitlementRepository: EntitlementRepository,
    private val globalOverlayManager: GlobalOverlayManager,
    private val coroutineScopeIo: CoroutineScope,
) : ViewEventFactory {

    private val navigateToIndexTabMap = mutableMapOf<IndexTab, ViewEventHandler>()
    override fun navigateToIndexTab(indexTab: IndexTab): ViewEventHandler {
        return navigateToIndexTabMap.getOrPut(indexTab) {
            ViewEventHandler.createOnClick {
                appStateManager.navigateToIndexTab(indexTab)
            }
        }
    }

    private val navigateToScreenIdMap: MutableMap<Id, ViewEventHandler> = mutableMapOf()
    override fun createNavigateToScreen(id: Id): ViewEventHandler {
        return navigateToScreenIdMap.getOrPut(id) {
            ViewEventHandler.createOnClick {
                appStateManager.navigateToScreen(id)
            }
        }
    }

    private val navigateToScreenArgumentMap: MutableMap<ScreenArgument, ViewEventHandler> =
        mutableMapOf()

    override fun createNavigateToScreen(argument: ScreenArgument): ViewEventHandler {
        return navigateToScreenArgumentMap.getOrPut(argument) {
            ViewEventHandler.createOnClick {
                appStateManager.navigateToScreen(argument)
            }
        }
    }

    override fun createOnClickNavigateToScreen(argument: ScreenArgument): () -> Unit = {
        appStateManager.navigateToScreen(argument)
    }

    override fun createOnClickNavigateToScreen(id: Id): () -> Unit = {
        appStateManager.navigateToScreen(id)
    }

    override fun createNavigateBack(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateBack()
        }
    }

    override fun createOnClickNavigateBack(): () -> Unit = {
        appStateManager.navigateBack()
    }

    override fun createNavigateToArtists(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateToArtists()
        }
    }

    override fun createNavigateToDataConsent(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateToDataConsent()
        }
    }

    override fun createNavigateToPaywall(
        autoTriggerPurchase: Boolean,
        subscriptionPlan: SubscriptionPlan?,
    ): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            val arbitratedSubscriptionPlan = when (entitlementRepository.licenseState.value) {
                LicenseStateType.AdFree -> SubscriptionPlan.PlusUnlimited
                LicenseStateType.Plus -> SubscriptionPlan.PlusBasic
                LicenseStateType.Unlicensed -> subscriptionPlan
            }
            appStateManager.navigateToPaywall(autoTriggerPurchase, subscriptionPlan = arbitratedSubscriptionPlan)
        }
    }

    override fun createNavigateToSearch(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateToSearch()
        }
    }

    override fun createNavigateToFirstRun(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateToFirstRun()
        }
    }

    override fun createNavigateToSignUp(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateToSignUp()
        }
    }

    override fun createNavigateToAccount(): ViewEventHandler =
        ViewEventHandler.createOnClick {
            appStateManager.navigateToAccount()
        }

    override fun createOnClickNavigateToDataConsent(): () -> Unit = {
        appStateManager.navigateToDataConsent()
    }

    override fun createNavigateToRewardAd(callbacks: RewardAdCallbacks): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateToRewardAd(callbacks)
        }
    }

    override fun createNavigateToOssLicenses(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateToOssLicenses()
        }
    }

    override fun createOnClickNavigateToSearch(): () -> Unit = {
        appStateManager.navigateToSearch()
    }

    private val onClickFavoriteMap: MutableMap<Id, ViewEventHandler> = mutableMapOf()

    override fun createOnClickFavorite(id: Id): ViewEventHandler {
        require(id !is Id.DesignId) { "Designs are no longer supported - use Remix instead (${id})" }

        return onClickFavoriteMap.getOrPut(id) {
            ViewEventHandler.createOnClick {
                coroutineScopeIo.launch {
                    favoriteItemsRepository.toggleFavorite(id)
                }
                inAppReviewRequestManager.requestReview()
            }
        }
    }

    override fun createArtistProfile(artist: ArtistState): ViewEventHandler? {
        return null
    }

    override fun createFollowing(
        artistId: Id.ArtistId,
        followState: FollowState?,
        actionBlock: ((Id.ArtistId) -> Unit)?
    ): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            actionBlock?.invoke(artistId)
            followingRepository.setFollowing(artistId, followState = followState)
        }
    }

    override fun createFollowOnly(
        artistId: Id.ArtistId,
        followState: FollowState?
    ): ViewEventHandler = ViewEventHandler.createOnClick {
        if (followState != null && followState.isFollowing == false) {
            followingRepository.setFollowing(artistId, followState = followState)
        }
    }

    override fun createFollowAndNavigateToArtist(
        artistId: Id.ArtistId,
        followState: FollowState?
    ): ViewEventHandler = ViewEventHandler.createOnClick {
        if (followState != null && followState.isFollowing == false) {
            followingRepository.setFollowing(artistId, followState = followState)
        }
        appStateManager.navigateToScreen(artistId)
    }

    override fun createOnClickOnboardingFinished(): () -> Unit = {
        onboardingManager.setOnboardingFinished(true)
    }

    override fun createOnboardingFinished() = ViewEventHandler.createOnClick {
        if (accountDataRepository.receiveNotifications.value) {
            systemPermissionManager.permissionGuardedAction(
                systemPermissionType = SystemPermissionType.PostNotifications,
                actionOnSuccess = {},
                actionOnDenied = {},
            )
        }
        onboardingManager.setOnboardingFinished(true)
    }

    override fun createOnClickHomeOnboardingFinished(): () -> Unit = {
        onboardingManager.setHomeOnboardingFinished(true)
    }

    override fun createHomeOnboardingFinished() = ViewEventHandler.createOnClick {
        onboardingManager.setHomeOnboardingFinished(true)
    }

    override fun createOnClickSetWallpaper(
        setWallpaper: () -> Unit
    ): () -> Unit = {
        setWallpaper()
        appStateManager.navigateBack()
    }

    override fun createSetWallpaper(
        setWallpaper: () -> Unit
    ): ViewEventHandler = ViewEventHandler.createOnClick {
        setWallpaper()
        appStateManager.navigateBack()
    }

    override fun createOnClickInitiatePurchase(purchasable: Purchasable, isSubscription: Boolean): (suspend () -> Unit) = {
        globalOverlayManager.show(GlobalOverlayViewStateDataScrim())
        purchaseManager.initiatePurchaseSuspend(purchasable, isSubscription)
        globalOverlayManager.hide()
    }

    override fun createInitiatePurchase(purchasable: Purchasable, isSubscription: Boolean): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            purchaseManager.initiatePurchase(purchasable, isSubscription)
        }
    }

    override fun createProfileImage(hasAccount: Boolean): ViewEventHandler {
        return if (hasAccount) {
            navigateToProfileImagePicker
        } else {
            navigateToIndexTab(IndexTab.Account)
        }
    }

    private val navigateToProfileImagePicker: ViewEventHandler = ViewEventHandler.createOnClick {
        profileImageManager.navigateToProfileImagePicker()
    }

    override fun createNavigateToProfileImagePicker(): ViewEventHandler =
        navigateToProfileImagePicker

    override fun createNavigateToAppMarketplace(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            systemNavigator.toSystemMarketplaceForCurrentApp()
        }
    }

    override fun createNavigateToUpdateApp(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            systemNavigator.toSystemMarketplaceForCurrentApp()
        }
    }

    override fun createNavigateToError(errorScreen: ErrorScreen): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            appStateManager.navigateToError(errorScreen)
        }
    }

    override fun createNavigateToUrl(url: String): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            systemNavigator.toUrl(url)
        }
    }

    private val navigateToSystemAppInfo: ViewEventHandler = ViewEventHandler.createOnClick {
        systemNavigator.toAppInfo()
    }

    override fun createNavigateToSystemAppInfo(): ViewEventHandler = navigateToSystemAppInfo

    override fun createNavigateToSystemNetworkSettings(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            systemNavigator.toSystemNetworkSettings()
        }
    }

    override fun createNavigateToSystemPhotos(): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            systemNavigator.toPhotos()
        }
    }

    override fun createShowAlert(alertViewState: AlertViewState): ViewEventHandler {
        return ViewEventHandler.createOnClick {
            alertManager.show(alertViewState)
        }
    }

    override fun createReceiveNewsletter(): (Boolean) -> Unit = {
        coroutineScopeIo.launch {
            accountDataRepository.updateReceiveNewsletter(it)
        }
    }

    override fun createReceiveNotifications(): (Boolean) -> Unit = { receiveNotifications ->
        coroutineScopeIo.launch {
            accountDataRepository.updateReceiveNotifications(receiveNotifications)
        }
    }

    override fun createRetryNetworkFetch(sideEffect: () -> Unit): ViewEventHandler = ViewEventHandler.createOnClick {
        sideEffect()
        networkRefreshManager.requestUserRefresh()
        appStateManager.navigateBack()
    }

}
