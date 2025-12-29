package wallapp.data.collection

import wallapp.content.model.Id.CollectionGroupId

// Note: this could likely be removed
data class CollectionGroup(
    val id: CollectionGroupId,
    val label: String,
    val collectionStates: List<CollectionState>,
)
