package wallapp.data.artist

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.data.following.FollowState
import wallapp.data.following.FollowingRepository
import wallapp.data.following.findById
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.log.Logger
import wallapp.random.RandomManager
import wallapp.util.combine


class ArtistStateRepositoryDefault(
    artistRepository: ArtistRepository,
    wallpaperRepository: WallpaperRepository,
    followingRepository: FollowingRepository,
    private val randomManager: RandomManager,
) : ArtistStateRepository {

    companion object {
        val Log = Logger("ArtistStateRepository")
    }

    private val deterministicRandom
        get() = randomManager.deterministicRandom

    private fun build(
        artists: List<Artist>,
        items: List<WallpaperItem>,
        followStates: List<FollowState>?,
    ): List<ArtistState> = build(
        artists,
        items.filterIsInstance<WallpaperCategory>(),
        items.filterIsInstance<WallpaperRemix>(),
        followStates,
    )

    private fun build(
        artists: List<Artist>,
        categories: List<WallpaperCategory> = emptyList(),
        remixes: List<WallpaperRemix> = emptyList(),
        followStates: List<FollowState>? = null,
    ): List<ArtistState> {
        return artists
            .map { it.map(categories, remixes, followStates) }
            .sortedBy { it.artist.name }
    }

    private fun Artist.map(
        allCategories: List<WallpaperCategory>,
        remixes: List<WallpaperRemix>,
        followStates: List<FollowState>?,
    ): ArtistState {

        /**
         * Note: it is critical to not assume that data is perfectly in sync here. This will be the
         * case when the bundled app data is different to the server data.
         * 
         * As an example, it's possible for [artist.singleAndCollectionCategoryIds] to contain
         * a category that is not present in [allCategories]. This will only happen momentarily
         * as data is propagated through the system, but the code should not make assumptions.
         *
         * As an example, use [firstOrNull] instead of [first].
         *
         * See #1710.
         */

        val artist = this

        val allWallpaperIds = artist.singleAndCollectionCategoryIds
            .flatMap { categoryId ->
                val remixIds = allCategories.firstOrNull { it.id == categoryId }?.remixIds
                if (remixIds == null) {
                    Log.w("Category not found: $categoryId for Artist ${artist.id}")
                }
                remixIds ?: emptyList()
            }
            .toSet()
            .ifEmpty { null }

        val feedWallpapers = remixes
            .filter { it.isSingle && it.artistId == artist.id }
            .shuffled(deterministicRandom)
            .ifEmpty { null }

        val previewWallpapers = allWallpaperIds
            ?.map { remixId -> remixes.first { remixId == it.id } }
            ?.shuffled(deterministicRandom)
            ?.take(ArtistState.MaxPreviewWallpapers)
            ?.ifEmpty { null }

        val followState = followStates?.findById(artist.id)

        return ArtistState(
            artist = artist,
            followState = followState,
            currentWallpaper = null,
            collectionIds = collectionIds,
            collectionStates = null, /* populated by [ContentRepository.getArtistDetail] */
            feedItemIds = feedWallpapers?.map { it.id },
            feedItems = feedWallpapers,
            previewWallpapers = previewWallpapers,
        )
    }

    override val artistStates: Flow<List<ArtistState>> = combine(
        artistRepository.artists,
        wallpaperRepository.allWallpaperItems,
        followingRepository.followStates,
        randomManager.randomSeed,
    ) { artists, allWallpaperItems, followStates, _ ->
        if (allWallpaperItems.isEmpty()) {
            return@combine emptyList<ArtistState>()
        }
        build(artists, allWallpaperItems, followStates)
    }

}