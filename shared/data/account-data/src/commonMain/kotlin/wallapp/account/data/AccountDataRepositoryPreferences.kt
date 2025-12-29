package wallapp.account.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.content.model.WallpaperDownloadEvent
import wallapp.coroutine.collectIn
import wallapp.devicerecord.DeviceRecord
import wallapp.devicerecord.mapDeviceRecordsFromExportString
import wallapp.log.Log
import wallapp.preferences.UserPreferences

class AccountDataRepositoryPreferences(
    private val accountDataDefaults: AccountDataDefaults,
    private val userPreferences: UserPreferences,
    private val coroutineScopeIo: CoroutineScope,
) : AccountDataRepository {

    override val acceptedTerms: MutableStateFlow<Boolean>
        get() = userPreferences.acceptedTerms
    override val reportUsageStats: MutableStateFlow<Boolean>
        get() = userPreferences.reportUsageStats

    override val hasSpecialCasePlusEntitlement: StateFlow<Boolean>
        get() = throw IllegalStateException("Only accessible through server")
    override val hasSpecialCaseIsDeveloper: StateFlow<Boolean>
        get() = throw IllegalStateException("Only accessible through server")

    override val favoriteIds: MutableStateFlow<List<Id>?> = MutableStateFlow(null)
    override val followingIds: MutableStateFlow<List<Id>?> = MutableStateFlow(null)
    override val purchaseRecords: MutableStateFlow<List<Id>?> = MutableStateFlow(null)
    override val receiveNewsletter: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val deviceRecords: MutableStateFlow<List<DeviceRecord>?> = MutableStateFlow(null)
    override val wallpaperDownloadEvents: MutableStateFlow<List<WallpaperDownloadEvent>?> = MutableStateFlow(null)
    override val receiveNotifications: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val cachedFavorites: MutableStateFlow<String>
        get() = userPreferences.favorites
    private val cachedFavoriteData: List<String>
        get() = cachedFavorites.value
            .split(Separator)
            .filter { it.isNotEmpty() }
    private val cachedFavoriteIds: Set<Id>
        get() = cachedFavoriteData
            .map { Id.fromExportShortString(it) }
            .toSet()
    private val cachedFavoriteIdStrings: Set<String>
        get() = cachedFavoriteData
            .toSet()

    private val cachedFollowings: MutableStateFlow<String>
        get() = userPreferences.followings
    private val cachedFollowingData: List<String>
        get() = cachedFollowings.value
            .split(Separator)
            .filter { it.isNotEmpty() }
    private val cachedFollowingIds: Set<Id>
        get() = cachedFollowingData
            .map { Id.fromExportShortString(it) }
            .toSet()
    private val cachedFollowingIdStrings: Set<String>
        get() = cachedFollowingData
            .toSet()

    private val cachedPurchases: MutableStateFlow<String>
        get() = userPreferences.purchases
    private val cachedPurchaseData: List<String>
        get() = cachedPurchases.value
            .split(Separator)
            .filter { it.isNotEmpty() }
    private val cachedPurchaseIds: Set<Id>
        get() = cachedPurchaseData
            .map { Id.fromExportShortString(it) }
            .toSet()
    private val cachedPurchaseIdStrings: Set<String>
        get() = cachedPurchaseData
            .toSet()
    private val cachedReceiveNewsletter: MutableStateFlow<Boolean>
        get() = userPreferences.joinNewsletter
    private val cachedDeviceInfo: MutableStateFlow<String>
        get() = userPreferences.deviceInfo
    private val cachedDeviceInfoData: List<DeviceRecord>
        get() = mapDeviceRecordsFromExportString(cachedDeviceInfo.value)

    private val cachedWallpaperDownloadEvent: MutableStateFlow<String>
        get() = userPreferences.wallpaperDownloadEvents
    private val cachedWallpaperDownloadEventsData: List<String>
        get() = cachedWallpaperDownloadEvent.value
            .split(Separator)
            .filter { it.isNotEmpty() }
    private val cachedWallpaperDownloadEvents: Set<WallpaperDownloadEvent>
        get() = cachedWallpaperDownloadEventsData
            .map { WallpaperDownloadEvent.fromExportString(it) }
            .toSet()
    private val cachedWallpaperDownloadEventsStrings: Set<String>
        get() = cachedWallpaperDownloadEventsData
            .toSet()
    private val cachedReceiveNotifications: MutableStateFlow<Boolean>
        get() = userPreferences.receiveNotifications

    init {
        cachedFavorites.collectIn(coroutineScopeIo) {
            favoriteIds.value = cachedFavoriteIds.toList()
        }
        cachedFollowings.collectIn(coroutineScopeIo) {
            followingIds.value = cachedFollowingIds.toList()
        }
        cachedPurchases.collectIn(coroutineScopeIo) {
            purchaseRecords.value = cachedPurchaseIds.toList()
        }
        cachedDeviceInfo.collectIn(coroutineScopeIo) {
            deviceRecords.value = cachedDeviceInfoData
        }
        cachedReceiveNewsletter.collectIn(coroutineScopeIo) {
            receiveNewsletter.value = cachedReceiveNewsletter.value
        }
        cachedWallpaperDownloadEvent.collectIn(coroutineScopeIo) {
            wallpaperDownloadEvents.value = cachedWallpaperDownloadEvents.toList()
        }
        cachedReceiveNotifications.collectIn(coroutineScopeIo) {
            receiveNotifications.value = cachedReceiveNotifications.value
        }
    }

    companion object {
        const val Separator = "<||>"
    }


    override suspend fun setIsFavorite(id: Id, isFavorite: Boolean): Boolean {
        Log.d("setFavorite($id, favorite: $isFavorite)")
        val cachedFavoriteIds = cachedFavoriteIdStrings
        val idExport = id.exportString
        if (isFavorite && !cachedFavoriteIds.contains(idExport)) {
            return updateCachedFavorites((cachedFavoriteIds + idExport).joinToString(Separator))
        } else if (!isFavorite && cachedFavoriteIds.contains(idExport)) {
            return updateCachedFavorites((cachedFavoriteIds - idExport).joinToString(Separator))
        }
        return false
    }

    override fun isFavorite(id: Id): Boolean {
        return cachedFavoriteIds.contains(id)
    }

    override suspend fun setIsFollowing(id: Id, isFollowing: Boolean): Boolean {
        Log.d("setIsFollowing($id, isFollowing: $isFollowing)")
        val cachedFollowingIds = cachedFollowingIdStrings
        val idExport = id.exportString
        if (isFollowing && !cachedFollowingIds.contains(idExport)) {
            return updateCachedFollowings((cachedFollowingIds + idExport).joinToString(Separator))
        } else if (!isFollowing && cachedFollowingIds.contains(idExport)) {
            return updateCachedFollowings((cachedFollowingIds - idExport).joinToString(Separator))
        }
        return false
    }
    override fun isFollowing(id: Id): Boolean {
        return cachedFollowingIds.contains(id)
    }

    override suspend fun setIsPurchased(id: Id, isPurchased: Boolean): Boolean {
        Log.d("setIsPurchased($id, isPurchased: $isPurchased)")
        val cachedPurchaseIds = cachedPurchaseIdStrings
        val idExport = id.exportString
        if (isPurchased && !cachedPurchaseIds.contains(idExport)) {
            return updateCachedPurchases((cachedPurchaseIds + idExport).joinToString(Separator))
        } else if (!isPurchased && cachedPurchaseIds.contains(idExport)) {
            return updateCachedPurchases((cachedPurchaseIds - idExport).joinToString(Separator))
        }
        return false
    }


    override fun isPurchased(id: Id): Boolean {
        return cachedPurchaseIds.contains(id)
    }

    private fun updateCachedFavorites(string: String): Boolean {
        cachedFavorites.value = string
        return true
    }
    private fun updateCachedFollowings(string: String): Boolean {
        cachedFollowings.value = string
        return true
    }
    private fun updateCachedPurchases(string: String): Boolean {
        cachedPurchases.value = string
        return true
    }
    private fun updateJoinNewsletter(isJoin: Boolean): Boolean {
        cachedReceiveNewsletter.value = isJoin
        return true
    }
    private fun updateCachedDeviceInfo(deviceInfo: String): Boolean {
        cachedDeviceInfo.value = deviceInfo
        return true
    }

    private fun updateCachedReceiveNotifications(receiveNotifications: Boolean): Boolean {
        cachedReceiveNotifications.value = receiveNotifications
        return true
    }

    private fun updateCachedWallpaperDownloadEvents(string: String): Boolean {
        cachedWallpaperDownloadEvent.value = string
        return true
    }
    override suspend fun updateAllFavorites(ids: List<Id>) {
        updateCachedFavorites(
            (cachedFavoriteIdStrings +
                    ids.filter { !cachedFavoriteIdStrings.contains(it.exportString) }
                        .map { it.exportString })
                .joinToString(Separator)
        )
    }

    override suspend fun updateAllFollowing(ids: List<Id>) {
        updateCachedFollowings(
            (cachedFollowingIdStrings +
                    ids.filter { !cachedFollowingIdStrings.contains(it.exportString) }
                        .map { it.exportString })
                .joinToString(Separator)
        )
    }

    override suspend fun updateAllPurchased(ids: List<Id>) {
        updateCachedFollowings(
            (cachedFollowingIdStrings +
                    ids.filter { !cachedFollowingIdStrings.contains(it.exportString) }
                        .map { it.exportString })
                .joinToString(Separator)
        )
    }

    override suspend fun updateReceiveNewsletter(receiveNewsletter: Boolean) {
        updateJoinNewsletter(receiveNewsletter)
    }

    override suspend fun updateDeviceInfo(deviceInfo: String) {
        updateCachedDeviceInfo(deviceInfo)
    }

    override suspend fun updateWallpaperDownloadEvent(wallpaperDownloadEvent: WallpaperDownloadEvent, isAdd: Boolean): Boolean {
        Log.d("updateWallpaperDownloadEvent($wallpaperDownloadEvent, isAdd: $isAdd)")
        val cachedWallpaperDownloadEvents = cachedWallpaperDownloadEventsStrings
        val wallpaperDownloadEventExport = wallpaperDownloadEvent.exportString
        if (isAdd && !cachedWallpaperDownloadEvents.contains(wallpaperDownloadEventExport)) {
            return updateCachedWallpaperDownloadEvents((cachedWallpaperDownloadEvents + wallpaperDownloadEventExport).joinToString(Separator))
        } else if (!isAdd && cachedWallpaperDownloadEvents.contains(wallpaperDownloadEventExport)) {
            return updateCachedWallpaperDownloadEvents((cachedWallpaperDownloadEvents - wallpaperDownloadEventExport).joinToString(Separator))
        }
        return false
    }

    override suspend fun updateAllWallpaperDownloadEvent(wallpaperDownloadEvents: List<WallpaperDownloadEvent>) {
        updateCachedWallpaperDownloadEvents(
            (cachedWallpaperDownloadEventsStrings +
                    wallpaperDownloadEvents.filter {
                        !cachedWallpaperDownloadEventsStrings.contains(
                            it.exportString
                        )
                    }
                        .map { it.exportString })
                .joinToString(Separator)
        )
    }

    override suspend fun updateReceiveNotifications(receiveNotifications: Boolean) {
        updateCachedReceiveNotifications(receiveNotifications)
    }

    override suspend fun deleteAll() {
        updateCachedFavorites("")
        updateCachedFollowings("")
        updateCachedPurchases("")
        updateReceiveNewsletter(accountDataDefaults.receiveNewsletter)
        updateCachedWallpaperDownloadEvents("")
        updateCachedReceiveNotifications(accountDataDefaults.receiveNotifications)
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
        favoriteIds?.let { updateAllFavorites(it) }
        followingIds?.let { updateAllFollowing(it) }
        purchaseRecords?.let { updateAllPurchased(it) }
        receiveNewsletter?.let { updateReceiveNewsletter(it) }
        deviceInfo?.let { updateDeviceInfo(it) }
        wallpaperDownloadEvents?.let { updateAllWallpaperDownloadEvent(it) }
        receiveNotifications?.let { updateReceiveNotifications(it) }
        return true
    }
}
