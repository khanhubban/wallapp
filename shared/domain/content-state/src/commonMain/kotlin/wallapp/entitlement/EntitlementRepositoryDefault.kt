package wallapp.entitlement

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.Id
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.DesignId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.Ids
import wallapp.content.model.WallpaperId
import wallapp.data.entitlement.EntitlementState
import wallapp.data.purchase.PurchaseRecordRepository
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.entitlement.EntitlementArbitrator.arbitrateEntitlementState
import wallapp.license.cache.LicenseCache
import wallapp.license.state.LicenseState
import wallapp.license.state.LicenseStateType
import wallapp.log.Logger
import wallapp.string.quote
import wallapp.util.combine

class EntitlementRepositoryDefault(
    private val licenseStateProvider: LicenseState,
    private val licenseCache: LicenseCache,
    private val purchaseRecordRepository: PurchaseRecordRepository,
    private val wallpaperRepository: WallpaperRepository,
    coroutineScopeMain: CoroutineScope,
) : EntitlementRepository {

    companion object {
        private val Log = Logger("EntitlementRepository")
    }

    override val licenseState: StateFlow<LicenseStateType>
        get() = licenseStateProvider.licenseStateType

    private val rewardUnlockedWallpapers: MutableStateFlow<String>
        get() = licenseCache.rewardUnlockedWallpapers
    private val rewardUnlockedWallpaperIds: StateFlow<List<RemixId>> =
        rewardUnlockedWallpapers.map { unsplit ->
            Ids.fromExportString(unsplit)?.ids?.filterIsInstance<RemixId>() ?: emptyList()
        }.stateIn(coroutineScopeMain, SharingStarted.Eagerly, emptyList())

    private val freeWallpaperIds: StateFlow<List<WallpaperId>> =
        wallpaperRepository.allWallpapers
            .map { allWallpapers ->
                allWallpapers
                    .filter { it.isFree }
                    .map { it.id }
            }
            .stateIn(coroutineScopeMain, SharingStarted.Eagerly, emptyList())

    private val allTrackIds: StateFlow<List<WallpaperId>> =
        wallpaperRepository.allTracks.map { wallpapers -> wallpapers.map { it.id } }
            .stateIn(coroutineScopeMain, SharingStarted.Eagerly, emptyList())

    private val purchasedCollectionIds: StateFlow<List<CollectionId>> =
        purchaseRecordRepository.purchasedCollectionIds
            .stateIn(coroutineScopeMain, SharingStarted.Eagerly, emptyList())
    private val purchasedWallpaperIds: StateFlow<List<RemixId>> =
        purchaseRecordRepository.purchasedWallpaperIds
            .stateIn(coroutineScopeMain, SharingStarted.Eagerly, emptyList())

    private val entitlementSummaryState: StateFlow<EntitlementSummaryState?> =
        combine(
            licenseStateProvider.licenseStateType,
            purchaseRecordRepository.purchasedCollectionIds,
            rewardUnlockedWallpaperIds,
            freeWallpaperIds,
        ) { licenseStateType, purchasedCollectionIds, rewardUnlockedIds, freeWallpaperIds ->
            EntitlementSummaryState.Unlocked(
                licenseStateType = licenseStateType,
                purchasedCollectionIds = purchasedCollectionIds,
                rewardUnlockedIds = rewardUnlockedIds,
                freeWallpaperIds = freeWallpaperIds,
            )
        }.stateIn(coroutineScopeMain, SharingStarted.Eagerly, null)

    override fun getEntitlementSummaryState(): EntitlementSummaryState? {
        return entitlementSummaryState.value
    }

    override fun setRewardEntitlement(id: Id) {
        validateId(id)
        addRewardUnlockedWallpaper(id)
    }

    private fun addRewardUnlockedWallpaper(id: Id) {
        val existingIds = Ids.fromExportString(rewardUnlockedWallpapers.value)
        if (existingIds?.ids?.contains(id) == true) {
            return
        }
        val ids = Ids((existingIds?.ids ?: emptyList()) + id)
        rewardUnlockedWallpapers.value = ids.exportString
    }

    override fun getEntitlementState(id: Id): Flow<EntitlementState> {
        validateId(id)

        return combine(
            licenseStateProvider.licenseStateType,
            purchaseRecordRepository.purchasedCollectionIds,
            purchaseRecordRepository.purchasedWallpaperIds,
            rewardUnlockedWallpaperIds,
            freeWallpaperIds,
            allTrackIds,
        ) { licenseStateType, purchasedCollectionIds, purchasedWallpaperIds, rewardUnlockedIds, freeWallpaperIds, allTrackIds ->
            arbitrateEntitlementState(
                licenseStateType = licenseStateType,
                isPurchased = purchasedCollectionIds.contains(id) || purchasedWallpaperIds.contains(id),
                isRewardUnlocked = rewardUnlockedIds.contains(id),
                isFree = freeWallpaperIds.contains(id),
                isInCollection = allTrackIds.contains(id),
            )
//                .also { Log.v("1.entitlementState: ${id}: $it") }
        }.onStart {
            arbitrateEntitlementState(
                licenseStateType = licenseStateProvider.licenseStateType.value,
                isPurchased = purchasedCollectionIds.value.contains(id) || purchasedWallpaperIds.value.contains(id),
                isRewardUnlocked = rewardUnlockedWallpaperIds.value.contains(id),
                isFree = freeWallpaperIds.value.contains(id),
                isInCollection = allTrackIds.value.contains(id),
            )
//                .also { Log.v("2.entitlementState: ${id}: $it") }
        }
    }

    private fun validateId(id: Id) {
        require(id !is CategoryId) { "Must use CollectionId instead for ${id.name.quote()}" }
        require(id !is DesignId) { "Designs are no longer supported - use RemixId instead (${id.name.quote()})" }
    }
}