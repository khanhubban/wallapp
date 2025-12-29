package wallapp.data.purchase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.account.data.AccountDataRepository
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.WallpaperId
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.log.Log

/**
 * Piggybacks on the [AccountDataRepository] to store the isPurchased state.
 */
class PurchaseRecordRepositoryDefault(
    private val accountDataRepository: AccountDataRepository,
    private val wallpaperRepository: WallpaperRepository,
    coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : PurchaseRecordRepository {

    private fun List<CollectionId>.createPurchaseRecords(): List<PurchaseRecord>? {
        return map { PurchaseRecord(it, isPurchased = true) }
            .ifEmpty { null }
    }

    override val purchaseRecords: StateFlow<List<PurchaseRecord>?> =
        accountDataRepository.purchaseRecords.map { purchases ->
            purchases
                ?.filterIsInstance<CollectionId>()
                ?.createPurchaseRecords()
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override val purchasedCollectionIds: Flow<List<CollectionId>> = purchaseRecords.map { purchaseRecords ->
        purchaseRecords
            ?.filter { it.isPurchased == true }
            ?.map { it.id }
            ?: emptyList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val purchasedWallpaperIds: Flow<List<WallpaperId>> = purchaseRecords.mapNotNull { purchaseRecords ->
        purchaseRecords
            ?.filter { it.isPurchased == true }
            ?.map { it.id }
            ?.asFlow()
    }.flatMapMerge { purchaseRecords ->
        purchaseRecords.flatMapMerge { collectionId ->
            wallpaperRepository
                .getRemixesForCategory(collectionId.categoryId)
                .map { remixes ->
                    remixes?.map { it.id } ?: emptyList()
                }
        }
    }.scan(emptySet<WallpaperId>()) { acc, ids ->
        acc + ids
    }.map { it.toList() }

    override fun getPurchaseRecord(id: CollectionId): Flow<PurchaseRecord> = flow {
        suspend fun emit(isPurchased: Boolean?) {
            emit(PurchaseRecord(id, isPurchased))
        }

        emit(isPurchased(id))

        purchaseRecords.collect { purchaseRecords ->
            emit(isPurchased = purchaseRecords?.contains(id))
        }
    }

    override fun setPurchased(id: CollectionId, isPurchased: Boolean) {
        coroutineScopeIo.launch {
            setPurchasedSuspend(id, isPurchased)
        }
    }

    suspend fun setPurchasedSuspend(id: CollectionId, isPurchased: Boolean) {
        accountDataRepository.setIsPurchased(id, isPurchased)
        Log.d("[PurchaseRecordRepository] setPurchased(): id: $id, isPurchased: $isPurchased")
    }

    override fun setAllPurchased(ids: List<CollectionId>) {
        coroutineScopeIo.launch {
            val existingIds = purchaseRecords.value?.map { it.id } ?: emptyList()
            val newIds = ids.filterNot { existingIds.contains(it) }
            if (newIds.isNotEmpty()) {
                accountDataRepository.updateAllPurchased(ids)
            }
        }
    }

    override fun isPurchased(id: CollectionId): Boolean {
        return purchaseRecords.value?.contains(id) ?: false
    }

    override fun resetAll() {
        val idsToReset = purchaseRecords.value?.map { it.id } ?: emptyList()
        idsToReset.forEach { id ->
            setPurchased(id, false)
        }
    }
}