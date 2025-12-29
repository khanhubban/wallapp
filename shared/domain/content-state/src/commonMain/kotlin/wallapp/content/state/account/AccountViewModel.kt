package wallapp.content.state.account

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import wallapp.account.Account
import wallapp.account.AccountError
import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.app.AppStateManager
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.error.ErrorScreen
import wallapp.coroutine.collectIn
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.graphics.Color
import wallapp.image.Image
import wallapp.license.state.LicenseStateRepository
import wallapp.log.Log
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionStatus
import wallapp.permission.SystemPermissionType
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertViewStateOk
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.privacymessaging.PrivacyMessagingManager
import wallapp.profileimage.ProfileImageManager
import wallapp.resources.string.StringRepository
import wallapp.result.ResultEx
import wallapp.system.navigation.SystemNavigator
import wallapp.theme.ColorToken
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

class AccountViewModel(
    private val appStateManager: AppStateManager,
    private val alertManager: AlertManager,
    private val viewStateFactory: ViewStateFactory,
    viewStateRefresher: ViewStateRefresher,
    private val accountManager: AccountManager,
    private val accountDataRepository: AccountDataRepository,
    private val profileImageManager: ProfileImageManager,
    private val licenseStateRepository: LicenseStateRepository,
    private val globalOverlayManager: GlobalOverlayManager,
    private val contentColorManager: ContentColorManager,
    private val strings: StringRepository,
    private val privacyMessagingManager: PrivacyMessagingManager,
    private val systemNavigator: SystemNavigator,
    private val systemPermissionManager: SystemPermissionManager,
) : ViewModel(), ScreenViewStateProvider {

    private var deleteAccountJob: Job? = null
    private var signOutJob: Job? = null

    private var navigatedToSettingsForNotificationsPermission = false

    private val accountViewEventSink: AccountViewEventSink = { event ->
        when (event) {
            AccountViewEvent.DeleteAccount -> {
                handleDeleteAccount()
            }
            AccountViewEvent.SignOut -> {
                handleSignOut()
            }
            AccountViewEvent.RestorePurchases -> {
                handleRestorePurchases()
            }

            AccountViewEvent.PrivacySettings -> {
                handlePrivacySettings()
            }

            AccountViewEvent.NotificationsTapToGrantPermission -> {
                systemPermissionManager.permissionGuardedAction(
                    SystemPermissionType.PostNotifications,
                    actionOnSuccess = {
                        updateReceiveNotifications(true)
                    },
                    actionOnDenied = {
                        toSystemNotificationsSettings()
                    }
                )
            }

            AccountViewEvent.ManageSubscription -> {
                appStateManager.navigateToManageSubscription()
            }
        }
    }

    private fun toSystemNotificationsSettings() {
        this.navigatedToSettingsForNotificationsPermission = true
        systemNavigator.toSystemNotificationsSettings()
    }

    private fun handleDeleteAccount() {
        Log.d("[firebase] [AccountViewModel] Deleting account, deleteAccountJob.isActive: ${deleteAccountJob?.isActive}")
        if (deleteAccountJob?.isActive == true) return
        deleteAccountJob = viewModelScope.launch {
            globalOverlayManager.show(viewStateFactory.createGlobalOverlay(strings.deletingYourAccount))
            when (val deleteResult = accountManager.deleteAccount()) {
                is ResultEx.Success -> {
                    globalOverlayManager.hide()
                    appStateManager.navigateBack()
                }

                is ResultEx.Error -> {
                    Log.e("[firebase] Failed to delete account")
                    globalOverlayManager.hide()
                    handleDeleteAccountError(deleteResult)
                }
            }
        }
    }

    private fun handleDeleteAccountError(deleteResult: ResultEx.Error) {
        CrashTrackingHolder.crashTracking.logNonFatalException(deleteResult.exception)
        when (deleteResult.exception) {
            is AccountError.NetworkConnectionError -> {
                appStateManager.navigateToError(ErrorScreen.Network())
            }
            else -> {
                alertManager.show(
                    AlertViewStateOk(
                        title = strings.deleteAccountErrorDialogTitle,
                        message = deleteResult.exception.message,
                    )
                )
            }
        }
    }

    private fun handleSignOut() {
        if (signOutJob?.isActive == true) return
        signOutJob = viewModelScope.launch {
            globalOverlayManager.show(viewStateFactory.createGlobalOverlay(strings.signingYouOut))
            Log.d("[firebase] [AccountViewModel] Signing out")
            accountManager.signOutCompletely()
            globalOverlayManager.hide()
            appStateManager.navigateBack()
        }
    }

    private fun handleRestorePurchases() {
        licenseStateRepository.checkLicenseState(forceUpdate = true)
    }

    private fun handlePrivacySettings() {
        privacyMessagingManager.showPrivacyOptions()
    }
    private val showPrivacySettings: Boolean
        get() = privacyMessagingManager.privacyOptionsRequired

    private fun createReceiveNotification(): (Boolean) -> Unit = { receiveNotifications ->
        viewModelScope.launch {
            if (receiveNotifications) {
                val permissionStatus = systemPermissionManager.postNotificationPermissionStatus.first()
                when (permissionStatus) {
                    is SystemPermissionStatus.Authorized -> {
                        updateReceiveNotifications(receiveNotifications)
                    }

                    is SystemPermissionStatus.Denied, is SystemPermissionStatus.NotDetermined -> {
                        systemPermissionManager.permissionGuardedAction(
                            SystemPermissionType.PostNotifications,
                            actionOnSuccess = {
                                updateReceiveNotifications(receiveNotifications)
                            },
                            actionOnDenied = {
                                if (systemPermissionManager.postNotificationPermissionStatus.first() is SystemPermissionStatus.Restricted) {
                                    toSystemNotificationsSettings()
                                }
                            }
                        )
                    }

                    is SystemPermissionStatus.Restricted -> {
                        toSystemNotificationsSettings()
                    }

                    else -> {
                        // TODO: Handle other possible statuses if applicable.
                    }
                }
            } else {
                updateReceiveNotifications(receiveNotifications)
            }
        }
    }

    private suspend fun updateReceiveNotifications(receiveNotifications: Boolean) {
        accountDataRepository.updateReceiveNotifications(receiveNotifications)
    }

    private fun createViewState(
        account: Account? = accountManager.signedInAccount.value,
        profileImage: Image = profileImageManager.profileImage.value,
        postNotificationPermissionStatus: SystemPermissionStatus = systemPermissionManager.postNotificationPermissionStatus.value,
        topBarContainerColor: Color = contentColorManager.profileTopBarContainer.value,
    ): AccountViewState {
        return viewStateFactory.createAccountViewState(
            account = account,
            profileImage = profileImage,
            reportUsageStats = accountDataRepository.reportUsageStats,
            receiveNotifications = MutableStateFlow(accountDataRepository.receiveNotifications.value),
            receiveNotificationsOnChangedEvent = createReceiveNotification(),
            postNotificationPermissionStatus = postNotificationPermissionStatus,
            topBarContainerColorToken = ColorToken.Custom(topBarContainerColor),
            subscribedToNewsletter = MutableStateFlow(accountDataRepository.receiveNewsletter.value),
            privacySettingsViewEvent = if (showPrivacySettings) { AccountViewEvent.PrivacySettings } else { null },
            accountViewEventSink = accountViewEventSink,
        )
    }

    override val viewState: StateFlow<AccountViewState> =
        combine(
            accountManager.signedInAccount,
            profileImageManager.profileImage,
            accountDataRepository.reportUsageStats,
            accountDataRepository.receiveNewsletter,
            accountDataRepository.receiveNotifications,
            systemPermissionManager.postNotificationPermissionStatus,
            contentColorManager.profileTopBarContainer,
            viewStateRefresher.refresh,
        ) { account, profileImage, _, _, _, postNotificationPermissionStatus, topBarColor, _ ->
            createViewState(
                account = account,
                profileImage = profileImage,
                postNotificationPermissionStatus = postNotificationPermissionStatus,
                topBarContainerColor = topBarColor,
            )
        }.stateIn(createViewState())

    init {
        systemPermissionManager.postNotificationPermissionStatus.collectIn(viewModelScope) {
            Log.d("[Permission] [AccountViewModel] postNotificationPermissionStatus: $it navigatedToSettingsForNotificationsPermission: $navigatedToSettingsForNotificationsPermission")
            if(navigatedToSettingsForNotificationsPermission) {
                if(it == SystemPermissionStatus.Authorized && navigatedToSettingsForNotificationsPermission) {
                    navigatedToSettingsForNotificationsPermission = false
                    updateReceiveNotifications(true)
                } else {
                    navigatedToSettingsForNotificationsPermission = false
                }
            }
        }
    }
}