package wallapp.account.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.Id
import wallapp.content.model.WallpaperDownloadEvent
import wallapp.devicerecord.DeviceRecord
import wallapp.log.Logger

class AccountDataRepositoryCombined(
    private val accountDataDefaults: AccountDataDefaults,
    private val accountDataRepositoryLocal: AccountDataRepository,
    private val accountDataRepositoryServer: AccountDataRepository?,
    coroutineScopeMain: CoroutineScope,
) : AccountDataRepositoryController {

    companion object {
        private val Log = Logger("[FirebaseSync] AccountDataRepositoryCombined")
    }

    // If the user is not signed in, we will use the local repository
    private val remoteProfileAvailable: Boolean
        get() = accountDataRepositoryServer?.getFirebaseUserId() != null

    private val accountDataRepositoryLocalActual: AccountDataRepository?
        get() = if (remoteProfileAvailable) null else accountDataRepositoryLocal

    override val acceptedTerms: MutableStateFlow<Boolean>
        get() = accountDataRepositoryLocal.acceptedTerms
    override val reportUsageStats: MutableStateFlow<Boolean>
        get() = accountDataRepositoryLocal.reportUsageStats

    override val hasSpecialCasePlusEntitlement: StateFlow<Boolean?>
        get() = accountDataRepositoryServer?.hasSpecialCasePlusEntitlement ?: MutableStateFlow(null)
    override val hasSpecialCaseIsDeveloper: StateFlow<Boolean?>
        get() = accountDataRepositoryServer?.hasSpecialCaseIsDeveloper ?: MutableStateFlow(null)

    override val favoriteIds: StateFlow<List<Id>?> = combine(
        accountDataRepositoryServer?.favoriteIds ?: flowOf(null),
        accountDataRepositoryLocal.favoriteIds,
    ) { serverIds, local ->
        val localIds = if (remoteProfileAvailable) null else local
        if (serverIds.isNullOrEmpty() && !localIds.isNullOrEmpty()) {
            localIds
        } else {
            serverIds
        }
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Lazily,
        initialValue = null,
    )
    override val followingIds: StateFlow<List<Id>?> = combine(
        accountDataRepositoryServer?.followingIds ?: flowOf(null),
        accountDataRepositoryLocal.followingIds,
    ) { serverIds, local ->
        val localIds = if (remoteProfileAvailable) null else local
        if (serverIds.isNullOrEmpty() && !localIds.isNullOrEmpty()) {
            localIds
        } else {
            serverIds
        }
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Lazily,
        initialValue = null,
    )
    override val purchaseRecords: StateFlow<List<Id>?> = combine(
        accountDataRepositoryServer?.purchaseRecords ?: flowOf(null),
        accountDataRepositoryLocal.purchaseRecords,
    ) { serverIds, local ->
        val localIds = if (remoteProfileAvailable) null else local
        if (serverIds.isNullOrEmpty() && !localIds.isNullOrEmpty()) {
            localIds
        } else {
            serverIds
        }
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Lazily,
        initialValue = null,
    )
    override val receiveNewsletter: StateFlow<Boolean> = combine(
        accountDataRepositoryServer?.receiveNewsletter ?: flowOf(null),
        accountDataRepositoryLocal.receiveNewsletter,
    ) { serverNewsletter, local ->
        val localNewsletter = if (remoteProfileAvailable) null else local
        // prioritize local data if it's available as server data returns a default value even if it's unavailable
        localNewsletter ?: serverNewsletter ?: accountDataDefaults.receiveNewsletter
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Lazily,
        initialValue = accountDataDefaults.receiveNewsletter,
    )

    override val receiveNotifications: StateFlow<Boolean> = combine(
        accountDataRepositoryServer?.receiveNotifications ?: flowOf(null),
        accountDataRepositoryLocal.receiveNotifications,
    ) { serverNotifications, local ->
        val localNotifications = if (remoteProfileAvailable) null else local
        // prioritize local data if it's available as server data returns a default value even if it's unavailable
        localNotifications ?: serverNotifications ?: accountDataDefaults.receiveNotifications
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Lazily,
        initialValue = accountDataDefaults.receiveNotifications,
    )

    override val deviceRecords: StateFlow<List<DeviceRecord>?> = combine(
        accountDataRepositoryServer?.deviceRecords ?: flowOf(null),
        accountDataRepositoryLocal.deviceRecords,
    ) { serverDeviceRecords, local ->
        val localDeviceRecords = if (remoteProfileAvailable) null else local
        if (serverDeviceRecords.isNullOrEmpty() && !localDeviceRecords.isNullOrEmpty()) {
            localDeviceRecords
        } else {
            serverDeviceRecords
        }
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Lazily,
        initialValue = null,
    )

    override val wallpaperDownloadEvents: StateFlow<List<WallpaperDownloadEvent>?> = combine(
        accountDataRepositoryServer?.wallpaperDownloadEvents ?: flowOf(null),
        accountDataRepositoryLocal.wallpaperDownloadEvents,
    ) { serverWallpaperDownloadEvents, local ->
        val localWallpaperDownloadEvents = if (remoteProfileAvailable) null else local
        if (serverWallpaperDownloadEvents.isNullOrEmpty() && !localWallpaperDownloadEvents.isNullOrEmpty()) {
            localWallpaperDownloadEvents
        } else {
            serverWallpaperDownloadEvents
        }
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    override suspend fun setIsFavorite(id: Id, isFavorite: Boolean): Boolean {
        Log.d("[firebase] setIsFavorite(id=$id, isFavorite=$isFavorite)")
        accountDataRepositoryLocalActual?.let {
            return it.setIsFavorite(id, isFavorite)
        }
        return accountDataRepositoryServer?.setIsFavorite(id, isFavorite) ?: false
    }

    override fun isFavorite(id: Id): Boolean {
        return favoriteIds.value?.contains(id) ?: false
    }

    override suspend fun setIsFollowing(id: Id, isFollowing: Boolean): Boolean {
        accountDataRepositoryLocalActual?.let {
            return it.setIsFollowing(id, isFollowing)
        }
        return accountDataRepositoryServer?.setIsFollowing(id, isFollowing) ?: false
    }

    override fun isFollowing(id: Id): Boolean {
        return followingIds.value?.contains(id) ?: false
    }

    override suspend fun setIsPurchased(id: Id, isPurchased: Boolean): Boolean {
        accountDataRepositoryLocalActual?.let {
            return it.setIsPurchased(id, isPurchased)
        }
        return accountDataRepositoryServer?.setIsPurchased(id, isPurchased) ?: false
    }

    override fun isPurchased(id: Id): Boolean {
        return purchaseRecords.value?.contains(id) ?: false
    }

    override suspend fun updateAllFavorites(ids: List<Id>) {
        Log.d("[firebase] ConnectionRepositoryCombined.updateAllFavorite(ids=$ids)")
        accountDataRepositoryLocalActual?.let {
            it.updateAllFavorites(ids)
            return
        }
        accountDataRepositoryServer?.updateAllFavorites(ids)
    }

    override suspend fun updateAllFollowing(ids: List<Id>) {
        Log.d("[firebase] ConnectionRepositoryCombined.updateAllFollowing(ids=$ids)")
        accountDataRepositoryLocalActual?.let {
            it.updateAllFollowing(ids)
            return
        }
        accountDataRepositoryServer?.updateAllFollowing(ids)
    }

    override suspend fun updateAllPurchased(ids: List<Id>) {
        Log.d("[firebase] ConnectionRepositoryCombined.updateAllPurchased(ids=$ids)")
        accountDataRepositoryLocalActual?.let {
            it.updateAllPurchased(ids)
            return
        }
        accountDataRepositoryServer?.updateAllPurchased(ids)
    }

    override suspend fun updateReceiveNewsletter(receiveNewsletter: Boolean) {
        Log.d("[firebase] AccountDataRepositoryCombined.updateNewsletter(isJoin=$receiveNewsletter), accountDataRepositoryLocalActual=$accountDataRepositoryLocalActual")
        accountDataRepositoryLocalActual?.let {
            it.updateReceiveNewsletter(receiveNewsletter)
            return
        }
        accountDataRepositoryServer?.updateReceiveNewsletter(receiveNewsletter)
    }

    override suspend fun updateDeviceInfo(deviceInfo: String) {
        Log.d("[firebase] AccountDataRepositoryCombined.updateDeviceInfo(deviceInfo=$deviceInfo)")
        accountDataRepositoryLocalActual?.let {
            it.updateDeviceInfo(deviceInfo)
            return
        }
        accountDataRepositoryServer?.updateDeviceInfo(deviceInfo)
    }

    override suspend fun updateWallpaperDownloadEvent(wallpaperDownloadEvent: WallpaperDownloadEvent, isAdd: Boolean): Boolean {
        Log.d("[firebase] AccountDataRepositoryCombined.updateWallpaperDownloadEvent(wallpaperDownloadEvent=$wallpaperDownloadEvent)")
        accountDataRepositoryLocalActual?.let {
           return it.updateWallpaperDownloadEvent(wallpaperDownloadEvent, isAdd)
        }
        return accountDataRepositoryServer?.updateWallpaperDownloadEvent(wallpaperDownloadEvent, isAdd) ?: false
    }

    override suspend fun updateAllWallpaperDownloadEvent(wallpaperDownloadEvents: List<WallpaperDownloadEvent>) {
        Log.d("[firebase] AccountDataRepositoryCombined.updateAllWallpaperDownloadEvent(wallpaperDownloadEvents=$wallpaperDownloadEvents)")
        accountDataRepositoryLocalActual?.let {
            it.updateAllWallpaperDownloadEvent(wallpaperDownloadEvents)
            return
        }
        accountDataRepositoryServer?.updateAllWallpaperDownloadEvent(wallpaperDownloadEvents)
    }

    override suspend fun updateReceiveNotifications(receiveNotifications: Boolean) {
        Log.d("[firebase] AccountDataRepositoryCombined.updateReceiveNotifications(receiveNotifications=$receiveNotifications)")
        accountDataRepositoryLocalActual?.let {
            it.updateReceiveNotifications(receiveNotifications)
            return
        }
        accountDataRepositoryServer?.updateReceiveNotifications(receiveNotifications)
    }

    override suspend fun deleteAll() {
        accountDataRepositoryServer?.deleteAll()
        accountDataRepositoryLocalActual?.deleteAll()
    }

    override suspend fun syncLocalConnectionsToRemote(): Boolean {
        val localFavoriteIds = accountDataRepositoryLocal.favoriteIds.value
        val localFollowingIds = accountDataRepositoryLocal.followingIds.value
        val localPurchaseRecords = accountDataRepositoryLocal.purchaseRecords.value
        val wallpaperDownloadEvents = accountDataRepositoryLocal.wallpaperDownloadEvents.value
        val receiveNewsletter = accountDataRepositoryLocal.receiveNewsletter.value
        val receiveNotifications = accountDataRepositoryLocal.receiveNotifications.value
        Log.d("[firebase] syncLocalConnectionsToRemote() localFavoriteIds=${localFavoriteIds?.size}, followingIds=${localFollowingIds?.size}, purchaseRecords=${localPurchaseRecords?.size}, wallpaperDownloadEvents=${wallpaperDownloadEvents}")

        return accountDataRepositoryServer?.updateAll(
            favoriteIds = localFavoriteIds,
            followingIds = localFollowingIds,
            purchaseRecords = localPurchaseRecords,
            wallpaperDownloadEvents = wallpaperDownloadEvents,
            receiveNewsletter = receiveNewsletter,
            receiveNotifications = receiveNotifications,
            canPostError = false, // This call can fail silently as the error is propagated up the call stack
        ) ?: false
    }

    override suspend fun deleteLocalConnections() {
        accountDataRepositoryLocal.deleteAll()
    }

    override suspend fun updateAll(
        favoriteIds: List<Id>?,
        followingIds: List<Id>?,
        purchaseRecords: List<Id>?,
        receiveNewsletter: Boolean?,
        deviceInfo: String?,
        wallpaperDownloadEvents: List<WallpaperDownloadEvent>?,
        receiveNotifications: Boolean?,
        canPostError: Boolean,
    ): Boolean {
        Log.d("[firebase] ConnectionRepositoryCombined.updateAll()")
        accountDataRepositoryLocalActual?.let {
            it.updateAll(
                favoriteIds,
                followingIds,
                purchaseRecords,
                receiveNewsletter,
                deviceInfo,
                wallpaperDownloadEvents,
                receiveNotifications,
            )
            return true
        }
        return accountDataRepositoryServer?.updateAll(
            favoriteIds,
            followingIds,
            purchaseRecords,
            receiveNewsletter,
            deviceInfo,
            wallpaperDownloadEvents,
            receiveNotifications,
            canPostError,
        ) ?: false
    }
}
