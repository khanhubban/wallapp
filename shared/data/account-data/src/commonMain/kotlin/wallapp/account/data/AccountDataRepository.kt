package wallapp.account.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.content.model.WallpaperDownloadEvent
import wallapp.devicerecord.DeviceRecord

interface AccountDataRepository {

    val acceptedTerms: MutableStateFlow<Boolean>
    val reportUsageStats: MutableStateFlow<Boolean>

    /**
     * Null means we haven't fetched the values from server yet
     */
    val hasSpecialCasePlusEntitlement: StateFlow<Boolean?>
    val hasSpecialCaseIsDeveloper: StateFlow<Boolean?>

    val favoriteIds: StateFlow<List<Id>?>
    /**
     * Returns true if a change was made to the underlying favorites data
     */
    suspend fun setIsFavorite(id: Id, isFavorite: Boolean): Boolean
    fun isFavorite(id: Id): Boolean
    suspend fun updateAllFavorites(ids: List<Id>)

    val followingIds: StateFlow<List<Id>?>
    suspend fun setIsFollowing(id: Id, isFollowing: Boolean): Boolean
    fun isFollowing(id: Id): Boolean
    suspend fun updateAllFollowing(ids: List<Id>)

    val purchaseRecords: StateFlow<List<Id>?>
    suspend fun setIsPurchased(id: Id, isPurchased: Boolean): Boolean
    fun isPurchased(id: Id): Boolean
    suspend fun updateAllPurchased(ids: List<Id>)

    val receiveNewsletter: StateFlow<Boolean>
    suspend fun updateReceiveNewsletter(receiveNewsletter: Boolean)

    val deviceRecords: StateFlow<List<DeviceRecord>?>
    suspend fun updateDeviceInfo(deviceInfo: String)

    val wallpaperDownloadEvents: StateFlow<List<WallpaperDownloadEvent>?>
    suspend fun updateWallpaperDownloadEvent(wallpaperDownloadEvent: WallpaperDownloadEvent, isAdd: Boolean): Boolean
    suspend fun updateAllWallpaperDownloadEvent(wallpaperDownloadEvents: List<WallpaperDownloadEvent>)

    val receiveNotifications: StateFlow<Boolean>
    suspend fun updateReceiveNotifications(receiveNotifications: Boolean)

    suspend fun deleteAll()

    suspend fun updateAll(
        favoriteIds: List<Id>? = null,
        followingIds: List<Id>? = null,
        purchaseRecords: List<Id>? = null,
        receiveNewsletter: Boolean? = null,
        deviceInfo: String? = null,
        wallpaperDownloadEvents: List<WallpaperDownloadEvent>? = null,
        receiveNotifications: Boolean? = null,
        canPostError: Boolean = true,
    ): Boolean

    fun getUserProfileId(): String? = null
    fun getFirebaseUserId(): String? = null
}
