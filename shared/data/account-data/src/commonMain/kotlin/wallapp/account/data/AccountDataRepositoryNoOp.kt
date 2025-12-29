package wallapp.account.data

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.content.model.Id
import wallapp.content.model.WallpaperDownloadEvent
import wallapp.devicerecord.DeviceRecord

object AccountDataRepositoryNoOp : AccountDataRepository {

    override val acceptedTerms: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val reportUsageStats: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val hasSpecialCasePlusEntitlement: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val hasSpecialCaseIsDeveloper: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val favoriteIds: MutableStateFlow<List<Id>?> = MutableStateFlow(null)
    override val followingIds: MutableStateFlow<List<Id>?> = MutableStateFlow(null)
    override val purchaseRecords: MutableStateFlow<List<Id>?> = MutableStateFlow(null)
    override val receiveNewsletter: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val deviceRecords: MutableStateFlow<List<DeviceRecord>?> = MutableStateFlow(null)
    override val wallpaperDownloadEvents: MutableStateFlow<List<WallpaperDownloadEvent>?> = MutableStateFlow(null)
    override val receiveNotifications: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override suspend fun setIsFavorite(id: Id, isFavorite: Boolean): Boolean {
        favoriteIds.value = favoriteIds.value.let { favoriteIds ->
            if (favoriteIds == null) {
                listOf(id)
            } else if (isFavorite) {
                (favoriteIds + id).distinct()
            } else {
                (favoriteIds - id).distinct()
            }
        }
        return true
    }

    override fun isFavorite(id: Id): Boolean {
        return favoriteIds.value?.contains(id) ?: false
    }

    override suspend fun setIsFollowing(id: Id, isFollowing: Boolean): Boolean {
        followingIds.value = followingIds.value.let { followingIds ->
            if (followingIds == null) {
                listOf(id)
            } else if (isFollowing) {
                (followingIds + id).distinct()
            } else {
                (followingIds - id).distinct()
            }
        }
        return true
    }

    override fun isFollowing(id: Id): Boolean {
        return followingIds.value?.contains(id) ?: false
    }


    override suspend fun setIsPurchased(id: Id, isPurchased: Boolean): Boolean {
        purchaseRecords.value = purchaseRecords.value.let { purchasedIds ->
            if (purchasedIds == null) {
                listOf(id)
            } else if (isPurchased) {
                (purchasedIds + id).distinct()
            } else {
                (purchasedIds - id).distinct()
            }
        }
        return true
    }

    override fun isPurchased(id: Id): Boolean {
        return purchaseRecords.value?.contains(id) ?: false
    }

    override suspend fun updateAllFavorites(ids: List<Id>) = Unit

    override suspend fun updateAllFollowing(ids: List<Id>) = Unit

    override suspend fun updateAllPurchased(ids: List<Id>) = Unit

    override suspend fun updateReceiveNewsletter(receiveNewsletter: Boolean) = Unit

    override suspend fun updateDeviceInfo(deviceInfo: String) = Unit

    override suspend fun updateWallpaperDownloadEvent(wallpaperDownloadEvent: WallpaperDownloadEvent, isAdd: Boolean): Boolean = true

    override suspend fun updateAllWallpaperDownloadEvent(wallpaperDownloadEvents: List<WallpaperDownloadEvent>) = Unit

    override suspend fun updateReceiveNotifications(receiveNotifications: Boolean) = Unit

    override suspend fun deleteAll() = Unit

    override suspend fun updateAll(
        favoriteIds: List<Id>?,
        followingIds: List<Id>?,
        purchaseRecords: List<Id>?,
        receiveNewsletter: Boolean?,
        deviceInfo: String?,
        wallpaperDownloadEvents: List<WallpaperDownloadEvent>?,
        receiveNotifications: Boolean?,
        canPostError: Boolean,
    ) = false
}
