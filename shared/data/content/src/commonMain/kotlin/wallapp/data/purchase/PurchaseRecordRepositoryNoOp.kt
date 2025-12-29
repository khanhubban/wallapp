package wallapp.data.purchase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.WallpaperId

object PurchaseRecordRepositoryNoOp : PurchaseRecordRepository {

    override val purchasedCollectionIds: Flow<List<CollectionId>> = flowOf(emptyList())

    override val purchasedWallpaperIds: Flow<List<WallpaperId>> = flowOf(emptyList())

    override val purchaseRecords: StateFlow<List<PurchaseRecord>?> = MutableStateFlow(null)

    override fun getPurchaseRecord(id: CollectionId): Flow<PurchaseRecord> =
        flowOf(PurchaseRecord(id, isPurchased = false))

    override fun isPurchased(id: CollectionId): Boolean = false

    override fun setPurchased(id: CollectionId, isPurchased: Boolean) { }

    override fun setAllPurchased(ids: List<CollectionId>) { }

    override fun resetAll() { }
}