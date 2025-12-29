package wallapp.data.collection

import wallapp.content.model.Id.CollectionId

data class CollectionConnectionState(
    val id: CollectionId,
    val isPurchased: Boolean?,
    val isUnlockedViaSubscription: Boolean?,
) {
    val isUnlocked: Boolean
        get() = isPurchased == true || isUnlockedViaSubscription == true
}
