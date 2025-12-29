package wallapp.entitlement

import wallapp.content.model.Id
import wallapp.content.model.Id.CollectionId
import wallapp.license.state.LicenseStateType
import wallapp.string.quote

sealed interface EntitlementSummaryState {

    val debugString: String

    class Unlocked(
        val licenseStateType: LicenseStateType,
        val purchasedCollectionIds: List<CollectionId>?,
        val rewardUnlockedIds: List<Id>?,
        val freeWallpaperIds: List<Id>?,
    ) : EntitlementSummaryState {
        override val debugString: String
            get() {
                val collectionIds = purchasedCollectionIds?.joinToString(
                    prefix = "  ",
                    separator = ",\n"
                ) { it.categoryId.name.quote() }

                val remixIds = rewardUnlockedIds?.joinToString(
                    prefix = "  ",
                    separator = ",\n"
                ) { it.name.quote() }

                return "licenseStateType: $licenseStateType" +
                        "\n\nPurchased Collection IDs: $collectionIds" +
                        "\n\nReward Unlocked IDs: $remixIds"
            }
    }

    data object UnlockAll : EntitlementSummaryState {
        override val debugString: String
            get() = "UnlockAll(debug)"
    }
}
