package wallapp.content.state

import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.PurchasableProductIds
import wallapp.content.model.WallpaperRemix
import wallapp.content.model.WallpaperRemixMock
import wallapp.content.model.WallpaperRemixPreviewImages
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistPreset
import wallapp.data.collection.CollectionConnectionState
import wallapp.data.collection.CollectionState
import wallapp.data.following.FollowState
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessMultipleCollectionsTest {

    val purchasableProductIds = PurchasableProductIds.Preset

    private fun Collection(
        id: Id.CollectionId,
        wallpapers: List<WallpaperRemix>,
        artist: Artist = ArtistPreset(),
        artistFollowState: FollowState?,
        label: String,
        connectionState: CollectionConnectionState?,
        secondaryId: String = "",
    ): CollectionState {
        return CollectionState(
            id = id,
            secondaryId = secondaryId,
            wallpapers = wallpapers,
            artist = artist,
            artistFollowState = artistFollowState,
            label = label,
            connectionState = connectionState,
            showAdFreeCollectionLockedInfo = false,
            purchasable = null,
        )
    }

    private fun createWallpaperRemix(i: Int): WallpaperRemix {
        return WallpaperRemixMock(
            id = Id.RemixId(name = "wallpaper$i"),
            label = "$i",
            artistId = ArtistId(name = "artist$i"),
            previewImages = WallpaperRemixPreviewImages.Preset,
        )
    }

    @Test fun processMultipleCollections_1collection_2wallpapers_returns1collection() {
        val wallpaperCount = 2
        val wallpaperToCollectionRatio = 2
        val collections = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "0",
                    wallpapers = listOf(
                        createWallpaperRemix(1),
                        createWallpaperRemix(2),
                        createWallpaperRemix(3),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            )
        )

        val randoms = List(1) { i -> Random(i) }
        val expectedWallpapers = listOf(
            collections[0].collectionState.wallpapers.shuffled(randoms[0])
        )

        val result = collections.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )

        val expected = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers[0],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            )
        )

        assertEquals(expected, result)

        // repeat processing with same input to ensure consistent output
        val result2 = collections.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )
        assertEquals(expected, result2)
    }

    @Test fun processMultipleCollections_1collection_4wallpapers_returns2collections() {
        val wallpaperCount = 4
        val wallpaperToCollectionRatio = 2
        val collections = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    wallpapers = listOf(
                        createWallpaperRemix(1),
                        createWallpaperRemix(2),
                        createWallpaperRemix(3),
                        createWallpaperRemix(4),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            )
        )

        val randoms = List(2) { i -> Random(i) }
        val expectedWallpapers = listOf(
            collections[0].collectionState.wallpapers.shuffled(randoms[0]),
            collections[0].collectionState.wallpapers.shuffled(randoms[1]),
        )

        val result = collections.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )

        val expected = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers[0],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "1",
                    wallpapers = expectedWallpapers[1],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            )
        )

        assertEquals(expected, result)

        // repeat processing with same input to ensure consistent output
        val result2 = collections.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )
        assertEquals(expected, result2)
    }

    @Test fun processMultipleCollections_2collections_10wallpapers_returns4collections() {
        val wallpaperCount = 10
        val wallpaperToCollectionRatio = 2
        val collections = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    wallpapers = listOf(
                        createWallpaperRemix(1),
                        createWallpaperRemix(2),
                        createWallpaperRemix(3),
                        createWallpaperRemix(4),
                        createWallpaperRemix(5),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    wallpapers = listOf(
                        createWallpaperRemix(6),
                        createWallpaperRemix(7),
                        createWallpaperRemix(8),
                        createWallpaperRemix(9),
                        createWallpaperRemix(10),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            )
        )

        val randoms = List(2) { i -> Random(i) }
        val expectedWallpapers = listOf(
            collections[0].collectionState.wallpapers.shuffled(randoms[0]),
            collections[0].collectionState.wallpapers.shuffled(randoms[1]),
            collections[1].collectionState.wallpapers.shuffled(randoms[0]),
            collections[1].collectionState.wallpapers.shuffled(randoms[1]),
        )

        val result = collections.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )

        val expected = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers[0],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "1",
                    wallpapers = expectedWallpapers[1],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers[2],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "1",
                    wallpapers = expectedWallpapers[3],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            )
        )

        assertEquals(expected, result)

        // repeat processing with same input to ensure consistent output
        val result2 = collections.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )
        assertEquals(expected, result2)
    }

    @Test fun processMultipleCollections_2collections_20wallpapers_1collectionAddedLater_returns6collections() {
        val wallpaperCount = 20
        val wallpaperToCollectionRatio = 2
        val collections = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    wallpapers = listOf(
                        createWallpaperRemix(1),
                        createWallpaperRemix(2),
                        createWallpaperRemix(3),
                        createWallpaperRemix(4),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    wallpapers = listOf(
                        createWallpaperRemix(5),
                        createWallpaperRemix(6),
                        createWallpaperRemix(7),
                        createWallpaperRemix(8),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            )
        )

        val randoms = List(4) { i -> Random(i) }
        val expectedWallpapers = listOf(
            collections[0].collectionState.wallpapers.shuffled(randoms[0]),
            collections[0].collectionState.wallpapers.shuffled(randoms[1]),
            collections[0].collectionState.wallpapers.shuffled(randoms[2]),
            collections[0].collectionState.wallpapers.shuffled(randoms[3]),
            collections[1].collectionState.wallpapers.shuffled(randoms[0]),
            collections[1].collectionState.wallpapers.shuffled(randoms[1]),
            collections[1].collectionState.wallpapers.shuffled(randoms[2]),
            collections[1].collectionState.wallpapers.shuffled(randoms[3]),
        )

        val result = collections.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )

        val expected = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers[0],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "1",
                    wallpapers = expectedWallpapers[1],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "2",
                    wallpapers = expectedWallpapers[2],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "3",
                    wallpapers = expectedWallpapers[3],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers[4],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "1",
                    wallpapers = expectedWallpapers[5],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "2",
                    wallpapers = expectedWallpapers[6],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "3",
                    wallpapers = expectedWallpapers[7],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            )
        )

        assertEquals(expected, result)

        // repeat processing with same input to ensure consistent output
        val result2 = collections.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )
        assertEquals(expected, result2)

        // add a collection and process again
        val collections2 = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    wallpapers = listOf(
                        createWallpaperRemix(1),
                        createWallpaperRemix(2),
                        createWallpaperRemix(3),
                        createWallpaperRemix(4),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    wallpapers = listOf(
                        createWallpaperRemix(5),
                        createWallpaperRemix(6),
                        createWallpaperRemix(7),
                        createWallpaperRemix(8),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection3"),
                    wallpapers = listOf(
                        createWallpaperRemix(9),
                        createWallpaperRemix(10),
                        createWallpaperRemix(11),
                        createWallpaperRemix(12),
                    ),
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            )
        )

        val randoms2 = List(3) { i -> Random(i) }
        val expectedWallpapers2 = listOf(
            collections2[0].collectionState.wallpapers.shuffled(randoms2[0]),
            collections2[0].collectionState.wallpapers.shuffled(randoms2[1]),
            collections2[0].collectionState.wallpapers.shuffled(randoms2[2]),
            collections2[1].collectionState.wallpapers.shuffled(randoms2[0]),
            collections2[1].collectionState.wallpapers.shuffled(randoms2[1]),
            collections2[1].collectionState.wallpapers.shuffled(randoms2[2]),
            collections2[2].collectionState.wallpapers.shuffled(randoms2[0]),
            collections2[2].collectionState.wallpapers.shuffled(randoms2[1]),
            collections2[2].collectionState.wallpapers.shuffled(randoms2[2]),
        )

        val result3 = collections2.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )

        val expected2 = listOf(
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers2[0],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "1",
                    wallpapers = expectedWallpapers2[1],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection1"),
                    secondaryId = "2",
                    wallpapers = expectedWallpapers2[2],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers2[3],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "1",
                    wallpapers = expectedWallpapers2[4],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection2"),
                    secondaryId = "2",
                    wallpapers = expectedWallpapers2[5],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection3"),
                    secondaryId = "0",
                    wallpapers = expectedWallpapers2[6],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection3"),
                    secondaryId = "1",
                    wallpapers = expectedWallpapers2[7],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            ),
            ContentState.Collection(
                collectionState = Collection(
                    id = Id.CollectionId(name = "collection3"),
                    secondaryId = "2",
                    wallpapers = expectedWallpapers2[8],
                    artistFollowState = null,
                    label = "",
                    connectionState = null
                ),
                viewSpec = CollectionPreviewViewSpec.Preset,
            )
        )

        assertEquals(expected2, result3)

        // repeat processing with same input to ensure consistent output
        val result4 = collections2.processMultipleContentStateCollections(
            wallpaperCount = wallpaperCount,
            wallpaperToCollectionRatio = wallpaperToCollectionRatio
        )
        assertEquals(expected2, result4)
    }
}