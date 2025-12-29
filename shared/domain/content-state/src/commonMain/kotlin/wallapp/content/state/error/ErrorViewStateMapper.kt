package wallapp.content.state.error

import androidx.compose.ui.unit.Dp
import wallapp.image.Image
import wallapp.network.NetworkConnectionState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.view.ViewEventHandler
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.system.platform.PlatformFeature
import wallapp.text.TextAlign
import wallapp.text.TextButtonLabel
import wallapp.text.TextErrorBody
import wallapp.text.TextErrorTitle
import wallapp.view.ViewEventFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewSpecFactory
import wallapp.view.menu.MenuItemFactory

class ErrorViewStateMapper(
    private val menuItemFactory: MenuItemFactory,
    private val imageRepository: ImageRepository,
    private val strings: StringRepository,
    private val viewSpecFactory: ViewSpecFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewEventFactory: ViewEventFactory,
) {
    private val paddingDefault: Dp
        get() = viewSpecArbitrator.paddingDefault
    private val paddingLarge: Dp
        get() = viewSpecArbitrator.paddingLarge
    private val actionButtonWidth: Dp
        get() = viewSpecArbitrator.heroButtonWidth
    private val errorViewSpec: ErrorViewSpec
        get() = viewSpecFactory.errorViewSpec
    private val imageSize: Dp
        get() = errorViewSpec.imageSize

    private fun createImage(image: Image): MenuItem {
        return menuItemFactory.createImage(
            image = image,
            size = imageSize,
        )
    }

    private fun createTitle(string: String): MenuItem {
        return menuItemFactory.centeredText(
            TextErrorTitle(string),
            width = null,
            height = null,
        )
    }

    private fun createMessage(
        string: String,
        centered: Boolean = true,
    ): MenuItem {
        val text = TextErrorBody(
            string = string,
            textAlign = if (centered) { TextAlign.Center } else { TextAlign.Start },
        )
        return if (centered) {
            menuItemFactory.centeredText(
                text,
                width = null,
                height = null,
            )
        } else {
            menuItemFactory.createLabel(
                text,
                width = null,
                height = null,
            )
        }
    }

    private fun createAppUpdateRequiredViewState(): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.appUpdateRequired),
            image = null,
            message1 = createMessage(strings.appUpdateRequiredMessage1),
            message2 = createMessage(strings.appUpdateRequiredMessage2),
            errorMessage = null,
            actionButton = if (PlatformFeature.CanOpenToSystemAppMarketplace) {
                menuItemFactory.createActionButton(
                    image = null,
                    text = TextButtonLabel(strings.updateAppAction),
                    horizontalPadding = paddingLarge,
                    eventHandler = viewEventFactory.createNavigateToUpdateApp(),
                )
            } else {
                null
            },
            closeButton = null,
        )
    }

    fun createDownloadFailedViewState(
        errorMessage: String,
    ): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.errorDownloadingWallpaper),
            image = null,
            message1 = createMessage(strings.errorDownloadingWallpaperMessage1),
            message2 = createMessage(strings.errorDownloadingWallpaperMessage2),
            errorMessage = TextStyleBody(errorMessage),
            actionButton = createOpenNetworkSettingsButton(),
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    fun createNetworkErrorViewState(showCloseButton: Boolean, networkConnectionState: NetworkConnectionState): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.networkConnectionRequired),
            image = createImage(imageRepository.networkError),
            message1 = createMessage(strings.networkErrorMessage1),
            message2 = createMessage(strings.networkErrorMessage2),
            errorMessage = TextStyleBody(networkConnectionState.asNetworkState()),
            actionButton = createOpenNetworkSettingsButton(),
            closeButton = if (showCloseButton) {
                menuItemFactory.createCloseButton()
            } else {
                null
            },
            allowNavigateBack = showCloseButton,
        )
    }

    fun createRemoteDataFetchErrorViewState(actionButtonHandler: ViewEventHandler): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.networkConnectionRequired),
            image = createImage(imageRepository.networkError),
            message1 = createMessage(strings.remoteDataFetchErrorMessage1, centered = false),
            message2 = createMessage(strings.remoteDataFetchErrorMessage2, centered = false),
            errorMessage = null,
            actionButton = createRetryNetworkFetchButton(actionButtonHandler),
            closeButton = null,
            allowNavigateBack = false,
        )
    }

    private fun createPermissionSystemMediaDeniedAndroid(): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.allowAccessToMedia),
            image = null,
            message1 = createMessage(
                strings.requiredToSaveWallpapersToTheSystemGallery,
                centered = true
            ),
            message2 = null,
            errorMessage = null,
            actionButton = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.goToSettings),
                width = actionButtonWidth,
                horizontalPadding = paddingLarge,
                eventHandler = viewEventFactory.createNavigateToSystemAppInfo(),
            ),
            actionButton2 = null,
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    private fun createPermissionSystemMediaDeniedIos(): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.allowAccessToPhotos),
            image = null,
            message1 = createMessage(
                strings.requiredToSaveWallpapersToPhotosMessage1,
                centered = true
            ),
            message2 = createMessage(
                strings.requiredToSaveWallpapersToPhotosMessage2,
                centered = true
            ),
            errorMessage = null,
            actionButton = null,
            actionButton2 = null,
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    fun createPurchaseErrorViewState(
        errorMessage: String,
        onCloseButtonViewEventHandler: ViewEventHandler,
    ): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.purchaseError),
            image = createImage(imageRepository.warning),
            message1 = createMessage(strings.purchaseErrorMessage1, centered = false),
            message2 = createMessage(strings.purchaseErrorMessage2, centered = false),
            errorMessage = TextStyleBody(errorMessage),
            actionButton = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.close),
                width = actionButtonWidth,
                horizontalPadding = paddingLarge,
                eventHandler = onCloseButtonViewEventHandler,
            ),
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    fun createSignInErrorViewState(
        errorMessage: String?,
        onRetryViewEventHandler: ViewEventHandler,
    ): ErrorViewState {
        val errorLabel = errorMessage?.let { TextStyleBody(strings.error(it)) }

        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.signInError),
            image = createImage(imageRepository.warning),
            message1 = createMessage(strings.signInErrorMessage1, centered = false),
            message2 = createMessage(strings.signInErrorMessage2, centered = false),
            errorMessage = errorLabel,
//            actionButton = menuItemFactory.createActionButton(
//                image = null,
//                text = TextButtonLabel(strings.done),
//                width = actionButtonWidth,
//                horizontalPadding = paddingLarge,
//                eventHandler = onRetryViewEventHandler,
//            ),
            actionButton = createOpenNetworkSettingsButton(),
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    private fun createSubscriptionExpiredErrorViewState(): ErrorViewState {
        val actionButton = menuItemFactory.createActionButton(
            image = imageRepository.plusIndicator,
            text = TextButtonLabel(strings.joinPlus),
            width = actionButtonWidth,
            horizontalPadding = paddingLarge,
            contentColorToken = null,
            eventHandler = viewEventFactory.createNavigateToPaywall(),
        )

        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.subscriptionExpiredErrorTitle),
            image = createImage(imageRepository.warning),
            message1 = createMessage(strings.subscriptionExpiredErrorMessage1, centered = true),
            message2 = createMessage(strings.subscriptionExpiredErrorMessage2, centered = false),
            errorMessage = null,
            actionButton = actionButton,
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    private fun createTemplateErrorViewState(): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle("Error Title"),
            image = createImage(imageRepository.warning),
            message1 = createMessage("Error Message 1 goes here.", centered = true),
            message2 = createMessage(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent eget nulla non velit tincidunt pellentesque. Vestibulum vitae viverra felis. Fusce eros lectus, ultrices sed faucibus non, viverra ut neque.",
                centered = true
            ),
            errorMessage = null,
            actionButton = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel("Action 1"),
                width = actionButtonWidth,
                horizontalPadding = paddingLarge,
                eventHandler = ViewEventHandler.NoOp,
            ),
            actionButton2 = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel("Action 2 (optional)"),
                width = actionButtonWidth,
                horizontalPadding = paddingLarge,
                eventHandler = ViewEventHandler.NoOp,
            ),
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    fun createBillingErrorViewState(
        onCloseButtonViewEventHandler: ViewEventHandler,
    ): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.billingErrorTitle),
            image = createImage(imageRepository.warning),
            message1 = createMessage(strings.billingErrorMessage1, centered = true),
            message2 = createMessage(strings.billingErrorMessage2, centered = true),
            errorMessage = null,
            actionButton = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.close),
                width = actionButtonWidth,
                horizontalPadding = paddingLarge,
                eventHandler = onCloseButtonViewEventHandler,
            ),
            actionButton2 = createOpenNetworkSettingsButton(),
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    fun createOpenNetworkSettingsButton(): MenuItem? =
        strings.networkErrorAction?.let {
            menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(it),
                width = actionButtonWidth,
                horizontalPadding = paddingLarge,
                eventHandler = viewEventFactory.createNavigateToSystemNetworkSettings(),
            )
        }

    fun createRetryNetworkFetchButton(eventHandler: ViewEventHandler): MenuItem? =
        strings.retry.let {
            menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(it),
                width = actionButtonWidth,
                horizontalPadding = paddingLarge,
                eventHandler = eventHandler,
            )
        }

    fun createUserProfileErrorViewState(errorMessage: String): ErrorViewState {
        return ErrorViewState.Data(
            viewSpec = viewSpecFactory.errorViewSpec,
            title = createTitle(strings.accountError),
            image = createImage(imageRepository.warning),
            message1 = createMessage(strings.userProfileErrorMessage1, centered = false),
            message2 = createMessage(strings.userProfileErrorMessage2, centered = false),
            errorMessage = TextStyleBody(errorMessage),
            actionButton = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.close),
                width = actionButtonWidth,
                horizontalPadding = paddingLarge,
                eventHandler = viewEventFactory.createNavigateBack(),
            ),
            closeButton = menuItemFactory.createCloseButton(),
        )
    }

    fun createViewState(errorScreen: ErrorScreen): ErrorViewState {
        return when (errorScreen) {
            ErrorScreen.AppUpdateRequired -> createAppUpdateRequiredViewState()
            is ErrorScreen.DownloadFailed -> throw IllegalArgumentException("Call createDownloadFailedViewState() directly")
            is ErrorScreen.Network -> throw IllegalArgumentException("Call createNetworkErrorViewState() directly")
            ErrorScreen.PermissionSystemMediaDeniedAndroid -> createPermissionSystemMediaDeniedAndroid()
            ErrorScreen.PermissionSystemMediaDeniedIos -> createPermissionSystemMediaDeniedIos()
            is ErrorScreen.Purchase -> throw IllegalArgumentException("Call createPurchaseErrorViewState() directly")
            ErrorScreen.RemoteDataFetch -> throw IllegalArgumentException("Call createRemoteDataFetchErrorViewState() directly")
            is ErrorScreen.RewardAd -> throw IllegalArgumentException("Call createRewardAdErrorViewState() directly")
            is ErrorScreen.SignIn -> throw IllegalArgumentException("Call createSignInErrorViewState() directly")
            ErrorScreen.SubscriptionExpired -> createSubscriptionExpiredErrorViewState()
            ErrorScreen.Template -> createTemplateErrorViewState()
            is ErrorScreen.UserProfile -> throw IllegalArgumentException("Call createUserProfileErrorViewState() directly")
        }
    }

}