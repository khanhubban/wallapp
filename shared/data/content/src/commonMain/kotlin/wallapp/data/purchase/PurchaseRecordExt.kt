package wallapp.data.purchase

import wallapp.content.model.Id


fun List<PurchaseRecord>?.findById(id: Id.CollectionId): PurchaseRecord? {
    return this?.find { it.id == id }
}

fun List<PurchaseRecord>?.contains(id: Id.CollectionId): Boolean {
    return findById(id)?.isPurchased ?: false
}
