package wallapp.data.collection

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import wallapp.content.model.Id
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryType
import wallapp.data.purchase.PurchaseRecord
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.util.combine

class CollectionRepositoryDefault(
    private val wallpaperRepository: WallpaperRepository,
) : CollectionRepository {

    private val categories: Flow<List<WallpaperCategory>>
        get() = wallpaperRepository.categories

    @OptIn(ExperimentalCoroutinesApi::class)
    override val collections: Flow<List<Collection>> = categories
        .flatMapMerge { categories ->
            flow {
                // For each category, fetch the collection and emit as a list
                emit(
                    categories.mapNotNull { category ->
                        if (category.categoryType == WallpaperCategoryType.Singles) {
                            null
                        } else {
                            getCollection(category.id.collectionId)
                                .firstOrNull()
                        }
                    }
                )
            }
        }

    override fun getCollection(id: Id.CollectionId): Flow<Collection?> = combine(
        wallpaperRepository.getCategory(id.categoryId),
        wallpaperRepository.getRemixesForCategory(id.categoryId),
    ) { category, remixes ->
        if (category != null && remixes != null) {
            Collection(
                id = Id.CollectionId(id.name),
                wallpapers = remixes,
                label = category.label,
                isFree = category.isFree,
            )
        } else {
            null
        }
    }

    private fun mapCollectionConnectionState(
        collectionId: Id.CollectionId,
        hasPlus: Boolean,
        purchaseRecord: PurchaseRecord,
    ): CollectionConnectionState {
        return CollectionConnectionState(
            id = collectionId,
            isPurchased = purchaseRecord.isPurchased,
            isUnlockedViaSubscription = hasPlus,
        )
    }

}