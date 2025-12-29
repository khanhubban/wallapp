package wallapp.data.collection

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.CollectionId

interface CollectionRepository {

    val collections: Flow<List<Collection>>

    fun getCollection(id: CollectionId): Flow<Collection?>
}