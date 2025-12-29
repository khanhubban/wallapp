package wallapp.data.purchase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.WallpaperId

interface PurchaseRecordRepository {

    val purchasedCollectionIds: Flow<List<CollectionId>>

    val purchasedWallpaperIds: Flow<List<WallpaperId>>

    val purchaseRecords: StateFlow<List<PurchaseRecord>?>

    fun getPurchaseRecord(id: CollectionId): Flow<PurchaseRecord>

    fun isPurchased(id: CollectionId): Boolean

    fun setPurchased(id: CollectionId, isPurchased: Boolean)

    fun setAllPurchased(ids: List<CollectionId>)

    fun resetAll()
}