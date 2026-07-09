package wallapp.content.state

import wallapp.content.model.Id
import wallapp.content.model.PurchasableProductIds
import wallapp.content.model.WallpaperRemix
import wallapp.content.model.WallpaperRemixMock
import wallapp.content.model.WallpaperRemixPreviewImages
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.data.artist.ArtistPreset
import wallapp.data.collection.CollectionState
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A singles-only catalog produces zero collections. Culling must survive that: the old inline
 * arithmetic computed `subList(0, size - diff)`, which is `subList(0, -n)` when the list is
 * empty, and threw before Explore could render.
 */
class CullCollectionsToWallpaperCountTest {

    @Suppress("unused")
    private val purchasableProductIds = PurchasableProductIds.Preset

    private fun collection(name: String): ContentState.Collection =
        ContentState.Collection(
            collectionState = CollectionState(
                id = Id.CollectionId(name = name),
                secondaryId = "",
                wallpapers = listOf(remix(1)),
                artist = ArtistPreset(),
                artistFollowState = null,
                label = "",
                connectionState = null,
                showAdFreeCollectionLockedInfo = false,
                purchasable = null,
            ),
            viewSpec = CollectionPreviewViewSpec.Preset,
        )

    private fun remix(i: Int): WallpaperRemix = WallpaperRemixMock(
        id = Id.RemixId(name = "wallpaper$i"),
        label = "$i",
        artistId = Id.ArtistId(name = "artist$i"),
        previewImages = WallpaperRemixPreviewImages.Preset,
    )

    @Test fun noCollections_returnsEmpty_ratherThanThrowing() {
        val result = emptyList<ContentState.Collection>().cullToWallpaperCount(wallpaperCount = 4)

        assertEquals(emptyList(), result)
    }

    @Test fun noCollections_noWallpapers_returnsEmpty() {
        val result = emptyList<ContentState.Collection>().cullToWallpaperCount(wallpaperCount = 0)

        assertEquals(emptyList(), result)
    }

    @Test fun moreCollectionsThanWallpapers_isCulledDownToWallpaperCount() {
        val collections = listOf(collection("a"), collection("b"), collection("c"))

        val result = collections.cullToWallpaperCount(wallpaperCount = 2)

        assertEquals(listOf(collection("a"), collection("b")), result)
    }

    @Test fun equalCounts_areLeftUntouched() {
        val collections = listOf(collection("a"), collection("b"))

        val result = collections.cullToWallpaperCount(wallpaperCount = 2)

        assertEquals(collections, result)
    }

    @Test fun fewerCollectionsThanWallpapers_areLeftUntouched() {
        val collections = listOf(collection("a"))

        val result = collections.cullToWallpaperCount(wallpaperCount = 4)

        assertEquals(collections, result)
    }
}
