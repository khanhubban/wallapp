package wallapp.view

import wallapp.ads.reward.RewardAdCallbacks
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.index.IndexTab
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.data.artist.ArtistState
import wallapp.data.following.FollowState
import wallapp.data.purchase.Purchasable
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.view.ViewEventHandler
import wallapp.screen.ScreenArgument

interface ViewEventFactory {

    fun navigateToIndexTab(indexTab: IndexTab): ViewEventHandler

    fun createNavigateToScreen(id: Id): ViewEventHandler
    fun createNavigateToScreen(argument: ScreenArgument): ViewEventHandler
    fun createOnClickNavigateToScreen(argument: ScreenArgument): () -> Unit
    fun createOnClickNavigateToScreen(id: Id): () -> Unit

    fun createNavigateBack(): ViewEventHandler
    fun createOnClickNavigateBack(): () -> Unit

    fun createNavigateToArtists(): ViewEventHandler
    fun createNavigateToDataConsent(): ViewEventHandler
    fun createNavigateToPaywall(autoTriggerPurchase: Boolean = false, subscriptionPlan: SubscriptionPlan? = null): ViewEventHandler
    fun createNavigateToSearch(): ViewEventHandler

    fun createNavigateToFirstRun(): ViewEventHandler
    fun createNavigateToSignUp(): ViewEventHandler

    fun createNavigateToAccount(): ViewEventHandler
    fun createOnClickNavigateToDataConsent(): () -> Unit
    fun createNavigateToRewardAd(callbacks: RewardAdCallbacks): ViewEventHandler
    fun createNavigateToOssLicenses(): ViewEventHandler
    fun createOnClickNavigateToSearch(): () -> Unit

    fun createOnClickFavorite(id: Id): ViewEventHandler

    fun createArtistProfile(artist: ArtistState): ViewEventHandler?
    fun createFollowing(artistId: ArtistId, followState: FollowState?, actionBlock: ((ArtistId) -> Unit)? = null): ViewEventHandler
    fun createFollowOnly(artistId: ArtistId, followState: FollowState?): ViewEventHandler
    fun createFollowAndNavigateToArtist(artistId: ArtistId, followState: FollowState?): ViewEventHandler

    fun createOnClickOnboardingFinished(): () -> Unit
    fun createOnboardingFinished(): ViewEventHandler
    fun createOnClickHomeOnboardingFinished(): () -> Unit
    fun createHomeOnboardingFinished(): ViewEventHandler

    fun createOnClickSetWallpaper(setWallpaper: () -> Unit): () -> Unit
    fun createSetWallpaper(setWallpaper: () -> Unit): ViewEventHandler

    fun createOnClickInitiatePurchase(purchasable: Purchasable, isSubscription: Boolean): suspend () -> Unit
    fun createInitiatePurchase(purchasable: Purchasable, isSubscription: Boolean): ViewEventHandler

    fun createProfileImage(hasAccount: Boolean): ViewEventHandler
    fun createNavigateToProfileImagePicker(): ViewEventHandler

    fun createNavigateToAppMarketplace(): ViewEventHandler
    fun createNavigateToUpdateApp(): ViewEventHandler

    fun createNavigateToError(errorScreen: ErrorScreen): ViewEventHandler

    fun createNavigateToUrl(url: String): ViewEventHandler

    fun createNavigateToSystemAppInfo(): ViewEventHandler
    fun createNavigateToSystemNetworkSettings(): ViewEventHandler
    fun createNavigateToSystemPhotos(): ViewEventHandler

    fun createShowAlert(alertViewState: AlertViewState): ViewEventHandler

    fun createReceiveNewsletter(): (Boolean) -> Unit

    fun createReceiveNotifications(): (Boolean) -> Unit
    fun createRetryNetworkFetch(sideEffect: () -> Unit): ViewEventHandler

}