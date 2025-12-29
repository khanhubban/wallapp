package wallapp.account.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import wallapp.coroutine.collectIn
import wallapp.device.DeviceCountry
import wallapp.devicerecord.DeviceRecordManager
import wallapp.devicerecord.mapDeviceRecordsToExportString
import wallapp.language.LanguageRepository
import wallapp.log.Logger
import wallapp.preferences.UserPreferences
import wallapp.time.TimeZoneRepository


class AccountDataManagerDefault(
    private val accountDataRepositoryController: AccountDataRepositoryController,
    private val accountDataRepository: AccountDataRepository,
    private val timeZoneRepository: TimeZoneRepository,
    private val deviceCountry: DeviceCountry,
    private val deviceRecordManager: DeviceRecordManager,
    private val languageRepository: LanguageRepository,
    private val userPreferences: UserPreferences,
    coroutineScopeIo: CoroutineScope,
) : AccountDataManager {

    companion object {
        val Log = Logger("[FirebaseSync] AccountDataManagerDefault")
    }

    // This is a Channel instead of a SharedFlow so that any emissions that occur while the
    // flow is not being collected are not lost. Also note: a channel can only be collected by
    // one collector at a time, so this will not work if it is collected by multiple collectors.
    private val _dataRefreshedChannel = Channel<Unit>(Channel.BUFFERED)
    override val dataRefreshed: Flow<Unit>
        get() = _dataRefreshedChannel.receiveAsFlow()

    override suspend fun syncLocalDataToRemote(): Boolean {
        Log.w("syncLocalDataToRemote()")
        if (accountDataRepositoryController.syncLocalConnectionsToRemote()) {
            accountDataRepositoryController.deleteLocalConnections()
            return true
        }
        return false
    }

    override suspend fun syncMetadataToRemote() {
        val fcmToken = userPreferences.firebaseCloudMessagingToken.firstOrNull()
        val deviceRecords = deviceRecordManager.getUpdatedDeviceRecords(fcmToken)
        // Persist per-user device info so the backend can reconcile FCM tokens across multiple devices.
        val deviceInfo = deviceRecords?.let { mapDeviceRecordsToExportString(it) }
        Log.w("syncMetadataToRemote() - hasToken: ${fcmToken != null}, recordCount: ${deviceRecords?.size ?: 0}")
        accountDataRepository.updateAll(
            deviceInfo = deviceInfo,
            canPostError = false, // This call can fail silently as it's not a user driven action
        )
    }

    override suspend fun deletePIIData() {
        Log.w("deleteServerData()")
        accountDataRepositoryController.deleteAll()
    }

    override suspend fun deleteLocalData() {
        Log.w("deleteLocalData()")
        accountDataRepositoryController.deleteLocalConnections()
    }

    private suspend fun onDataUpdated(via: String) {
        Log.d("onDataUpdated() - $via")
        _dataRefreshedChannel.send(Unit)
    }

    init {
        userPreferences.firebaseCloudMessagingToken.filter { it.isNotEmpty() }.collectIn(coroutineScopeIo) {
            onDataUpdated("firebaseCloudMessagingToken")
        }
    }

}
