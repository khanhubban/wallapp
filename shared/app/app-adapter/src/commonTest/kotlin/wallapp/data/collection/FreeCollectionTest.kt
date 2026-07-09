package wallapp.data.collection

import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.PurchasableProductIds
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryType
import wallapp.content.model.WallpaperRemixMock
import wallapp.content.model.WallpaperRemixPreviewImages
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * A Collection with no purchasableProductIds is free: it carries no store product, so there is
 * nothing to buy and nothing to gate. Catalogs published by the content pipeline contain exactly
 * such collections.
 */
class FreeCollectionTest {

    private fun category(
        type: WallpaperCategoryType,
        productIds: PurchasableProductIds?,
    ) = WallpaperCategory(
        id = CategoryId(name = "stillscenes~featured"),
        label = "Featured",
        artistId = ArtistId(name = "stillscenes"),
        featureBannerImageMediaHolder = null,
        categoryType = type,
        previewRemix = WallpaperRemixMock(
            id = RemixId(name = "stillscenes_0e59bd05"),
            label = "Alpine Dawn",
            artistId = ArtistId(name = "stillscenes"),
            previewImages = WallpaperRemixPreviewImages.Preset,
        ),
        remixIds = listOf(RemixId(name = "stillscenes_0e59bd05")),
        purchasableProductIds = productIds,
    )

    @Test fun collectionWithoutProductIds_isConstructible_andFree() {
        val collection = category(WallpaperCategoryType.Collection, productIds = null)

        assertTrue(collection.isFree)
        assertNull(collection.purchasableProductIds)
    }

    @Test fun collectionWithProductIds_isNotFree() {
        val collection = category(WallpaperCategoryType.Collection, PurchasableProductIds.Preset)

        assertFalse(collection.isFree)
    }

    @Test fun freeCollection_isUnlocked_withoutPurchaseOrSubscription() {
        val state = CollectionConnectionState(
            id = CollectionId(name = "stillscenes~featured"),
            isPurchased = false,
            isUnlockedViaSubscription = false,
            isFree = true,
        )

        assertTrue(state.isUnlocked)
    }

    @Test fun paidCollection_staysLocked_withoutPurchaseOrSubscription() {
        val state = CollectionConnectionState(
            id = CollectionId(name = "dark~stripes"),
            isPurchased = false,
            isUnlockedViaSubscription = false,
            isFree = false,
        )

        assertFalse(state.isUnlocked)
    }

    @Test fun paidCollection_unlocksOnPurchaseOrSubscription() {
        val id = CollectionId(name = "dark~stripes")

        assertTrue(
            CollectionConnectionState(id, isPurchased = true, isUnlockedViaSubscription = false, isFree = false)
                .isUnlocked
        )
        assertTrue(
            CollectionConnectionState(id, isPurchased = false, isUnlockedViaSubscription = true, isFree = false)
                .isUnlocked
        )
    }

    @Test fun collectionPurchasable_isNullForAFreeCollection() {
        val free = category(WallpaperCategoryType.Collection, productIds = null)

        assertNull(CollectionPurchasable.orNull(free))
    }

    @Test fun collectionPurchasable_isBuiltForAPaidCollection() {
        val paid = category(WallpaperCategoryType.Collection, PurchasableProductIds.Preset)

        val purchasable = CollectionPurchasable.orNull(paid)

        assertEquals(CollectionId(name = "stillscenes~featured"), purchasable?.id)
        assertEquals(PurchasableProductIds.Preset, purchasable?.purchasableProductIds)
    }
}
