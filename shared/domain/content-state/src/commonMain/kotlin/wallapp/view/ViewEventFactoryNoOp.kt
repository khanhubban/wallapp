package wallapp.view

import wallapp.ads.reward.RewardAdCallbacks
import wallapp.content.model.Id
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.index.IndexTab
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.data.artist.ArtistState
import wallapp.data.following.FollowState
import wallapp.data.purchase.Purchasable
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.view.OnClickNoOp
import wallapp.pixel.view.OnClickNoOpSuspend
import wallapp.pixel.view.ViewEventHandler
import wallapp.screen.ScreenArgument

object ViewEventFactoryNoOp : ViewEventFactory {
    override fun navigateToIndexTab(indexTab: IndexTab) = ViewEventHandler.NoOp

    override fun createNavigateToScreen(id: Id) = ViewEventHandler.NoOp

    override fun createNavigateToScreen(argument: ScreenArgument) = ViewEventHandler.NoOp

    override fun createOnClickNavigateToScreen(argument: ScreenArgument) = OnClickNoOp

    override fun createOnClickNavigateToScreen(id: Id) = OnClickNoOp

    override fun createNavigateBack() = ViewEventHandler.NoOp

    override fun createOnClickNavigateBack() = OnClickNoOp

    override fun createNavigateToArtists() = ViewEventHandler.NoOp

    override fun createNavigateToDataConsent() = ViewEventHandler.NoOp

    override fun createNavigateToPaywall(autoTriggerPurchase: Boolean, subscriptionPlan: SubscriptionPlan?) = ViewEventHandler.NoOp

    override fun createNavigateToSearch() = ViewEventHandler.NoOp

    override fun createNavigateToFirstRun() = ViewEventHandler.NoOp

    override fun createNavigateToSignUp() = ViewEventHandler.NoOp

    override fun createNavigateToAccount() = ViewEventHandler.NoOp

    override fun createOnClickNavigateToDataConsent() = OnClickNoOp

    override fun createNavigateToRewardAd(callbacks: RewardAdCallbacks) = ViewEventHandler.NoOp

    override fun createOnClickNavigateToSearch() = OnClickNoOp

    override fun createNavigateToOssLicenses() = ViewEventHandler.NoOp

    override fun createOnClickFavorite(id: Id) = ViewEventHandler.NoOp

    override fun createArtistProfile(artist: ArtistState) = ViewEventHandler.NoOp

    override fun createFollowing(
        artistId: Id.ArtistId,
        followState: FollowState?,
        actionBlock: ((Id.ArtistId) -> Unit)?,
    ): ViewEventHandler = ViewEventHandler.NoOp

    override fun createFollowOnly(
        artistId: Id.ArtistId,
        followState: FollowState?,
    ): ViewEventHandler = ViewEventHandler.NoOp

    override fun createFollowAndNavigateToArtist(
        artistId: Id.ArtistId,
        followState: FollowState?,
    ): ViewEventHandler = ViewEventHandler.NoOp

    override fun createOnClickOnboardingFinished() = OnClickNoOp

    override fun createOnboardingFinished() = ViewEventHandler.NoOp

    override fun createOnClickHomeOnboardingFinished() = OnClickNoOp

    override fun createHomeOnboardingFinished() = ViewEventHandler.NoOp

    override fun createOnClickSetWallpaper(setWallpaper: () -> Unit) = OnClickNoOp

    override fun createSetWallpaper(setWallpaper: () -> Unit) = ViewEventHandler.NoOp

    override fun createOnClickInitiatePurchase(purchasable: Purchasable, isSubscription: Boolean) = OnClickNoOpSuspend

    override fun createInitiatePurchase(purchasable: Purchasable, isSubscription: Boolean) = ViewEventHandler.NoOp

    override fun createProfileImage(hasAccount: Boolean) = ViewEventHandler.NoOp

    override fun createNavigateToProfileImagePicker() = ViewEventHandler.NoOp

    override fun createNavigateToAppMarketplace() = ViewEventHandler.NoOp

    override fun createNavigateToUpdateApp() = ViewEventHandler.NoOp

    override fun createNavigateToError(errorScreen: ErrorScreen) = ViewEventHandler.NoOp

    override fun createNavigateToUrl(url: String) = ViewEventHandler.NoOp

    override fun createNavigateToSystemAppInfo() = ViewEventHandler.NoOp

    override fun createNavigateToSystemNetworkSettings() = ViewEventHandler.NoOp

    override fun createNavigateToSystemPhotos() = ViewEventHandler.NoOp

    override fun createShowAlert(alertViewState: AlertViewState) = ViewEventHandler.NoOp

    override fun createReceiveNewsletter(): (Boolean) -> Unit = { }

    override fun createReceiveNotifications(): (Boolean) -> Unit = { }

    override fun createRetryNetworkFetch(sideEffect: () -> Unit): ViewEventHandler = ViewEventHandler.NoOp
}