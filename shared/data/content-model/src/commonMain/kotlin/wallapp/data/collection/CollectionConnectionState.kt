package wallapp.data.collection

import wallapp.content.model.Id.CollectionId

data class CollectionConnectionState(
    val id: CollectionId,
    val isPurchased: Boolean?,
    val isUnlockedViaSubscription: Boolean?,
    /** A collection with no store product cannot be bought, so it is never locked. */
    val isFree: Boolean = false,
) {
    val isUnlocked: Boolean
        get() = isFree || isPurchased == true || isUnlockedViaSubscription == true
}
