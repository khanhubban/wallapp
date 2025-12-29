package wallapp.permission

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import platform.Photos.PHAccessLevelAddOnly
import platform.Photos.PHAccessLevelReadWrite
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIViewController
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import wallapp.coroutine.collectIn
import wallapp.log.Logger
import wallapp.permission.SystemPermissionMapperIos.toSystemPermissionStatusPh
import wallapp.permission.SystemPermissionMapperIos.toSystemPermissionStatusUn
import wallapp.system.notificationcenter.NotificationCenterIos
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.currentUiViewController

class SystemPermissionManagerIos(
    private val uiControllerManager: UiControllerManager,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
    internal val photoPermissionType: PhotoPermissionTypeIos = PhotoPermissionTypeIos.Default,
) : SystemPermissionManager {

    companion object {
        val Log = Logger("SystemPermissionManagerIos")
    }

    override val systemMediaPermissionStatus: MutableStateFlow<SystemPermissionStatus>
        get() = when (photoPermissionType) {
            PhotoPermissionTypeIos.AddToPhotos -> addToPhotosPermissionStatus
            PhotoPermissionTypeIos.FullPhotosLibrary -> fullPhotosLibraryPermissionStatus
        }

    private val addToPhotosPermissionStatus: MutableStateFlow<SystemPermissionStatus> =
        MutableStateFlow(SystemPermissionStatus.Unknown)
    private val fullPhotosLibraryPermissionStatus: MutableStateFlow<SystemPermissionStatus> =
        MutableStateFlow(SystemPermissionStatus.Unknown)


    private val _postNotificationPermissionStatus =
        MutableStateFlow<SystemPermissionStatus>(SystemPermissionStatus.Unknown)
    override val postNotificationPermissionStatus: StateFlow<SystemPermissionStatus> = _postNotificationPermissionStatus

    private val SystemPermissionType.stateFlow: MutableStateFlow<SystemPermissionStatus>
        get() = when (this) {
            is SystemPermissionType.SystemMedia -> systemMediaPermissionStatus
            is SystemPermissionType.PostNotifications -> _postNotificationPermissionStatus
        }

    val currentUiViewController: UIViewController?
        get() = uiControllerManager.currentUiViewController

    private fun requestPermission(systemPermissionType: SystemPermissionType): Flow<SystemPermissionStatus> {
        return callbackFlow {
            val callback: (SystemPermissionStatus) -> Unit = { status ->
                trySendBlocking(status)
                close()
            }
            when (systemPermissionType) {
                is SystemPermissionType.PostNotifications -> {
                    requestPostNotificationPermission(callback)
                }
                is SystemPermissionType.SystemMedia -> {
                    requestPhotoLibraryPermission(callback)
                }
            }
            awaitClose {
                Log.d("closing requestPermission channel - $systemPermissionType")
            }
        }
    }

    private fun requestPostNotificationPermission(completionHandler: (SystemPermissionStatus) -> Unit) {
        Log.d("requestPostNotificationPermission()")
        UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
            options = (UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge),
            completionHandler = { granted, error ->
                Log.d("Requesting post notification permission: granted: $granted, error: $error")
                val status = if (error != null) {
                    SystemPermissionStatus.Unknown
                } else {
                    if (granted) {
                        SystemPermissionStatus.Authorized
                    } else {
                        // If not granted, we can't differentiate between Denied and Restricted without further context
                        SystemPermissionStatus.Denied
                    }
                }
                _postNotificationPermissionStatus.value = status
                completionHandler(status)
            }
        )
        checkCurrentNotificationStatus()
    }

    private fun checkCurrentNotificationStatus() {
        UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
            coroutineScopeMain.launch {
                _postNotificationPermissionStatus.value =
                    settings?.authorizationStatus?.let { toSystemPermissionStatusUn(it) }
                        ?: SystemPermissionStatus.Unknown
            }
        }
    }

    private fun requestPhotoLibraryPermission(completionHandler: (SystemPermissionStatus) -> Unit) {
        when (photoPermissionType) {
            PhotoPermissionTypeIos.AddToPhotos -> requestAddToPhotosPermission(completionHandler)
            PhotoPermissionTypeIos.FullPhotosLibrary -> requestFullPhotoLibraryPermission(completionHandler)
        }
    }

    private fun checkPhotoLibraryPermission() {
        when (photoPermissionType) {
            PhotoPermissionTypeIos.AddToPhotos -> checkAddToPhotosPermission()
            PhotoPermissionTypeIos.FullPhotosLibrary -> checkFullPhotoLibraryPermission()
        }
    }

    private fun requestFullPhotoLibraryPermission(completionHandler: (SystemPermissionStatus) -> Unit) {
        Log.d("Requesting Full Photo Library permission")
        PHPhotoLibrary.requestAuthorizationForAccessLevel(PHAccessLevelReadWrite) { phStatus ->
            val status = toSystemPermissionStatusPh(phStatus)
            Log.d("Full Photo Library permission result/status: $status")
            fullPhotosLibraryPermissionStatus.value = status
            completionHandler(status)
        }
    }

    private fun checkFullPhotoLibraryPermission() {
        val status = PHPhotoLibrary.authorizationStatusForAccessLevel(PHAccessLevelReadWrite)
        fullPhotosLibraryPermissionStatus.value = toSystemPermissionStatusPh(status)
    }

    private fun requestAddToPhotosPermission(completionHandler: (SystemPermissionStatus) -> Unit) {
        Log.d("Requesting Add to Photos permission")
        PHPhotoLibrary.requestAuthorizationForAccessLevel(PHAccessLevelAddOnly) { phStatus ->
            val status = toSystemPermissionStatusPh(phStatus)
            Log.d("Add to Photos permission result/status: $status")
            addToPhotosPermissionStatus.value = status
            completionHandler(status)
        }
    }

    private fun checkAddToPhotosPermission() {
        val status = PHPhotoLibrary.authorizationStatusForAccessLevel(PHAccessLevelAddOnly)
        addToPhotosPermissionStatus.value = toSystemPermissionStatusPh(status)
    }

    override fun refreshPermissionStatus() {
        Log.d("Refreshing all permission statuses")
        coroutineScopeIo.launch {
            checkCurrentNotificationStatus()
            checkPhotoLibraryPermission()
        }
    }

    override fun permissionGuardedAction(
        systemPermissionType: SystemPermissionType,
        actionOnSuccess: suspend () -> Unit,
        actionOnDenied: suspend () -> Unit
    ) {
        coroutineScopeMain.launch {
            val permissionStatus = systemPermissionType.stateFlow.value
            if (permissionStatus.isAuthorizedOrLimited()) {
                actionOnSuccess()
            } else {
                val updatedPermission = requestPermission(systemPermissionType).firstOrNull()
                if (updatedPermission?.isAuthorizedOrLimited() == true) {
                    actionOnSuccess()
                } else {
                    actionOnDenied()
                }
            }
        }
    }

    init {
        Log.d("Init")

        NotificationCenterIos.addWillEnterForegroundNotification { refreshPermissionStatus() }

        systemMediaPermissionStatus.collectIn(coroutineScopeIo) {
            Log.d("System media permission status: $it")
        }
        postNotificationPermissionStatus.collectIn(coroutineScopeIo) {
            Log.d("Post notification permission status: $it")
        }
    }
}


val SystemPermissionManager.photoPermissionTypeIos: PhotoPermissionTypeIos
    get() = (this as? SystemPermissionManagerIos)?.photoPermissionType ?: PhotoPermissionTypeIos.Default
