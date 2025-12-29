package wallapp.content.state

import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentResult.ExploreContentResult
import wallapp.data.content.ContentSort.distributeWallpapersByArtist
import wallapp.data.highlight.Highlights
import wallapp.random.RandomManager
import wallapp.util.isEven
import wallapp.view.ViewSpecFactory
import kotlin.math.max
import kotlin.random.Random

class ContentStateMapperDefault(
    private val viewSpecFactory: ViewSpecFactory,
    private val randomManager: RandomManager
) : ContentStateMapper {

    private val deterministicRandom
        get() = randomManager.deterministicRandom

    private val wallpaperToCollectionRatio = 3

    private fun List<ContentState>?.feedOrNull(): ContentStateFeed? {
        return if (this.isNullOrEmpty()) {
            null
        } else {
            ContentStateFeed(this)
        }
    }

    override fun mapExplore(explore: ExploreContentResult): ContentStateFeed? {
        return mapExplore(
            wallpapers = explore.wallpapers,
            collectionStates = explore.collectionStates,
            highlights = explore.highlights,
            topmostIds = explore.topmostIds,
        ).feedOrNull()
    }

    override fun mapExploreForChunks(explore: ExploreContentResult): ContentStateFeed? {
        return mapExploreForChunks(
            wallpapers = explore.wallpapers,
            collectionStates = explore.collectionStates,
            highlights = explore.highlights,
            topmostIds = explore.topmostIds,
        ).feedOrNull()
    }

    private fun mapExploreForChunks(
        wallpapers: List<Wallpaper>?,
        collectionStates: List<CollectionState>?,
        highlights: List<Highlights>?,
        topmostIds: List<Id>,
    ): List<ContentState>? {
        val wallpaperContent = wallpapers
            ?.shuffled(deterministicRandom)
            ?.distributeWallpapersByArtist()
            ?.map { ContentState.Wallpaper(it) }
            ?: emptyList()
        val collectionContent = collectionStates
            ?.map { ContentState.Collection(it, viewSpecFactory.collectionPreviewSmallViewSpec) }
            ?: emptyList()
        val highlightsContent = highlights
            ?.map { ContentState.Highlights(it) }
            ?: emptyList()

        // Early-exit until all content is available (#175)
        if (wallpaperContent.isEmpty() || collectionContent.isEmpty() || highlightsContent.isEmpty()) {
            return null
        }

        val multipleCollections = if (collectionContent.isNotEmpty()) {
            collectionContent.processMultipleContentStateCollections(
                wallpaperCount = wallpaperContent.size,
                wallpaperToCollectionRatio = 1,
            )
        } else {
            collectionContent
        }.shuffled(deterministicRandom)

        val culledCollections = if (wallpaperContent.size != multipleCollections.size) {
            // remove extra collections
            val diff = wallpaperContent.size - multipleCollections.size
            multipleCollections.subList(0, multipleCollections.size - diff)
        } else {
            multipleCollections
        }

        // interleave wallpaper and collection content
        val contentStates = interleaveCollectionsBetweenWallpapersForChunks(
            wallpaperContent,
            culledCollections
        )

        if (contentStates.isEmpty()) {
            return null
        }

        return contentStates
            .let {
                mutableListOf<ContentState>().apply {
                    if (highlightsContent.isNotEmpty()) {
                        add(highlightsContent.first())
                    }
                    addAll(it)
                }
            }
    }

    private fun mapExplore(
        wallpapers: List<Wallpaper>?,
        collectionStates: List<CollectionState>?,
        highlights: List<Highlights>?,
        topmostIds: List<Id>,
    ): List<ContentState>? {
        val wallpaperContent = wallpapers
            ?.shuffled(deterministicRandom)
            ?.distributeWallpapersByArtist()
            ?.map { ContentState.Wallpaper(it) }
            ?: emptyList()
        val collectionContent = collectionStates
            ?.map { ContentState.Collection(it, viewSpecFactory.collectionPreviewSmallViewSpec) }
            ?: emptyList()
        val highlightsContent = highlights
            ?.map { ContentState.Highlights(it) }
            ?: emptyList()

        // Early-exit until all content is available (#175)
        if (wallpaperContent.isEmpty() || collectionContent.isEmpty()) {
            return null
        }

        val multipleCollections = if (collectionContent.isNotEmpty()) {
            collectionContent.processMultipleContentStateCollections(
                wallpaperCount = wallpaperContent.size,
                wallpaperToCollectionRatio = wallpaperToCollectionRatio,
            )
        } else {
            collectionContent
        }.shuffled(deterministicRandom)

        val contentStates = interleaveCollectionsBetweenWallpapers(
            wallpaperContent,
            multipleCollections
        )

        if (contentStates.isEmpty()) {
            return null
        }

        return contentStates
            .let {
                mutableListOf<ContentState>().apply {
                    if (highlightsContent.isNotEmpty()) {
                        add(highlightsContent.first())
                    }
                    addAll(it)
                }
            }
    }

    private fun interleaveCollectionsBetweenWallpapers(
        wallpapers: List<ContentState.Wallpaper>,
        collections: List<ContentState.Collection>
    ): MutableList<ContentState> {
        val random = deterministicRandom
        val contentStates = mutableListOf<ContentState>()
        if (wallpapers.size > collections.size) {
            var prevWallIndex = 0
            var collectionIndex = 0

            fun addContentBasedOn(step: Int) {
                val endIndex = prevWallIndex + step
                if (endIndex > prevWallIndex && endIndex < wallpapers.size) {
                    contentStates.addAll(
                        wallpapers.subList(
                            prevWallIndex,
                            endIndex
                        )
                    )
                }
                prevWallIndex = endIndex
                if (collectionIndex < collections.size) {
                    contentStates.add(collections[collectionIndex])
                    collectionIndex++
                }
            }
            
            // Special case for first collection
            addContentBasedOn(
                step = if (randomManager.randomSeed.value.isEven()) {
                    random.nextInt(0, 2)
                } else {
                    random.nextInt(2, 4)
                }
            )
            while (prevWallIndex < wallpapers.size) {
                addContentBasedOn(
                    step = random.nextInt(
                        max(wallpaperToCollectionRatio - 1, 1),
                        wallpaperToCollectionRatio + 2
                    )
                )
            }

        }
        return contentStates
    }

    fun interleaveCollectionsBetweenWallpapersForChunks(
        wallpapers: List<ContentState.Wallpaper>,
        collections: List<ContentState.Collection>
    ): MutableList<ContentState> {
        val result = mutableListOf<ContentState>()
        val maxSize = maxOf(wallpapers.size, collections.size)

        for (i in 0 until maxSize) {
            if (i < wallpapers.size) result.add(wallpapers[i])
            if (i < collections.size) result.add(collections[i])
        }

        return result
    }

    private fun List<ContentState>.applyTopmostIds(topmostIds: List<Id>): List<ContentState> {
        val topMostIds = topmostIds.map { it.name }
        val topMostContent = mutableListOf<ContentState>()
        forEach {
            val id = it.id ?: return@forEach
            if (id.name in topMostIds) {
                topMostContent.add(it)
            }
        }
        return topMostContent
            .distinctBy { it.id?.name }
            .sortedBy {
                val id = it.id ?: return@sortedBy 0
                topMostIds.indexOf(id.name)
            } + this
    }
}

fun List<ContentState.Collection>.processMultipleContentStateCollections(
    wallpaperCount: Int,
    wallpaperToCollectionRatio: Int,
): List<ContentState.Collection> {
    val originalCollections = this
    var collectionCountMultiplier = 1
    if (wallpaperToCollectionRatio == 1) {
        collectionCountMultiplier = wallpaperCount / this.size
    } else {
        while (wallpaperCount / (this.size * collectionCountMultiplier) > wallpaperToCollectionRatio) {
            collectionCountMultiplier++
        }
    }

    val randoms = List(collectionCountMultiplier) { i -> Random(i) }
    val finalCollections = mutableListOf<ContentState.Collection>()
    originalCollections.forEach { collection ->
        repeat(collectionCountMultiplier) { i ->
            finalCollections.add(
                collection.copy(
                    collectionState = collection.collectionState.copy(
                        secondaryId = i.toString(),
                        wallpapers = collection.collectionState.wallpapers.shuffled(randoms[i])
                    )
                )
            )
        }
    }

    return finalCollections
}