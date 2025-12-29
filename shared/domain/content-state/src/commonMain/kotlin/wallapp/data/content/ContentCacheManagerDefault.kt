package wallapp.data.content

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import wallapp.collection.mutableMapOfConcurrent
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.FolderId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.Wallpaper
import wallapp.content.state.ContentState
import wallapp.content.state.exhibit.ExhibitViewState
import wallapp.content.state.explore.ExploreRepository
import wallapp.content.state.wallpaper.WallpaperPreviewViewSpec
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.coroutine.CoroutineScopeIo
import wallapp.coroutine.collectIn
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistRepository
import wallapp.data.artist.ArtistState
import wallapp.data.content.media.ContentMediaRepository
import wallapp.data.folder.FolderStateRepository
import wallapp.data.highlight.Highlight
import wallapp.data.highlight.Highlight.ArtistHighlight
import wallapp.data.highlight.Highlight.CollectionHighlight
import wallapp.data.highlight.Highlight.FolderHighlight
import wallapp.data.highlight.Highlight.PlusHighlight
import wallapp.data.highlight.Highlight.SignInHighlight
import wallapp.data.highlight.Highlight.WallpaperHighlight
import wallapp.data.highlight.Highlights
import wallapp.data.wallpaper.WallpaperState
import wallapp.image.ImageViewSpecFactory
import wallapp.image.cache.ImageCacheSpec.Companion.ImageCacheSpecCool
import wallapp.image.imageUrl
import wallapp.image.prefetch.ImagePrefetchEntry
import wallapp.image.prefetch.ImagePrefetcher
import wallapp.image.sized.SizedImage
import wallapp.media.model.MediaHolder
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.view.ViewEventHandler
import wallapp.util.CancellableWork
import wallapp.view.ViewSpecFactory
import wallapp.view.ViewStateMapper

class ContentCacheManagerDefault(
    private val config: ContentCacheConfig,
    private val contentRepository: ContentRepository,
    private val contentMediaRepository: ContentMediaRepository,
    private val exploreRepository: ExploreRepository,
    private val artistRepository: ArtistRepository,
    private val folderStateRepository: FolderStateRepository,
    private val imagePrefetcher: ImagePrefetcher,
    private val viewStateMapper: ViewStateMapper,
    private val viewSpecFactory: ViewSpecFactory,
    private val imageViewSpecFactory: ImageViewSpecFactory,
    @CoroutineScopeIo private val coroutineScopeIo: CoroutineScope,
) : ContentCacheManager {

    companion object {
//        val Log = Logger("ContentCacheManager")
    }

    override val prefetchWallpaperImagesEnabled: Boolean
        get() = config.prefetchWallpaperImages

    private val isUiReady: StateFlow<Boolean>
        get() = config.isUiReady

    suspend fun waitForUiReady() {
        isUiReady
            .filter { it }
            .first()
    }

    /**
     * Always returns true. Used in rare instances where data can be prefetched without waiting
     * for UI to settle (such as on the FirstRun screen).
     */
    private val isUiSettledTrue: StateFlow<Boolean> = MutableStateFlow(true)

    private val imagePrefetchJobs: MutableMap<ImagePrefetchEntry, CancellableWork> =
        mutableMapOfConcurrent()

    private fun mapWallpaperPreviewViewState(
        wallpaper: Wallpaper,
        viewSpec: WallpaperPreviewViewSpec,
    ): WallpaperPreviewViewState {
        return viewStateMapper.mapWallpaperPreviewViewState(
            wallpaper = wallpaper,
            viewSpec = viewSpec,
            onClick = ViewEventHandler.NoOp,
            onClickFavorite = ViewEventHandler.NoOp,
            onClickPlus = null,
        )
    }

    private fun prefetchWallpaperPreviewToDisc(
        wallpaper: Wallpaper,
        viewSpec: WallpaperPreviewViewSpec,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
        val id = wallpaper.id
        val logId = "$id @ [$visibleIndex]"

        if (!isUiSettled.value) {
//            Log.w("prefetchWallpaperPreviewToDisc(): $logId - early exit - UI unsettled")
            return
        }

        val viewState = mapWallpaperPreviewViewState(wallpaper, viewSpec)

        prefetchImageViewStateToDisc(id.name, viewState.imageViewState, isUiSettled, logId)
    }

    private fun prefetchImageViewStateToDisc(
        id: String,
        imageViewState: ImageViewState,
        isUiSettled: StateFlow<Boolean>,
        logId: String,
    ) {
        if (!prefetchWallpaperImagesEnabled) {
//            Log.w("prefetchWallpaperPreviewToDisc(): $logId - early exit - prefetch disabled")
            return
        }

        val imagePrefetchEntry = ImagePrefetchEntry.from(
            id = id,
            imageViewState = imageViewState,
            imageCacheSpec = ImageCacheSpecCool,
        )

        if (imagePrefetchEntry == null) {
//            Log.w("prefetchImageViewStateToDisc(): $logId - early exit - image URL is null")
            return
        }

        imagePrefetchJobs[imagePrefetchEntry]?.also {
//            Log.d("prefetchImageViewStateToDisc(): $logId Cancelling previous prefetch job for wallpaper")
            it.cancel()
            imagePrefetchJobs.remove(imagePrefetchEntry)
        }

//        Log.i("prefetchImageViewStateToDisc(): START $logId, ${imagePrefetchEntry.imageViewState.image.imageUrl}")
        val cancellableWork = imagePrefetcher.prefetch(imagePrefetchEntry, onCompletion = {
//            Log.i("prefetchImageViewStateToDisc(): FINISH $logId - job completed for image - ${imagePrefetchEntry.imageViewState.image.imageUrl}")
            imagePrefetchJobs.remove(imagePrefetchEntry)
        })
        if (cancellableWork != null) {
            imagePrefetchJobs[imagePrefetchEntry] = cancellableWork
            coroutineScopeIo.launch {
                isUiSettled.first { !it }
//                Log.w("prefetchImageViewStateToDisc(): $logId UI unsettled - Cancelling prefetch to disc for wallpaper")
                cancellableWork.cancel()
                imagePrefetchJobs.remove(imagePrefetchEntry)
            }
        }
    }

    private val artistImageViewSpecs: List<Pair<ImageViewSpec, SizedImage>>
        get() = listOf(
            imageViewSpecFactory.artistProfileImageSmallImageViewSpec to
                    SizedImage.ArtistSmall,
            imageViewSpecFactory.artistProfileImageMediumImageViewSpec to
                    SizedImage.ArtistMedium,
        )

    private fun prefetchArtistPreviewHeroToDisc(
        artist: Artist,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
        prefetchArtistPreviewHeroToDisc(
            artistId = artist.id,
            mediaHolder = artist.profileImageMediaHolder,
            isUiSettled = isUiSettled,
            visibleIndex = visibleIndex,
        )
    }

    private fun prefetchArtistPreviewHeroToDisc(
        artistId: Id.ArtistId,
        mediaHolder: MediaHolder,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
        artistImageViewSpecs.forEach { (imageViewSpec, sizedImage) ->
            val logId = "$artistId @ [$visibleIndex/$sizedImage]"

            val imageViewState = contentMediaRepository.getImageViewState(
                mediaHolder = mediaHolder,
                sizedImage = sizedImage,
                imageViewSpec,
            )
            prefetchImageViewStateToDisc(artistId.name, imageViewState, isUiSettled, logId)
        }
    }

    private fun prefetchWallpaperPreviewHeroToDisc(
        wallpaperState: WallpaperState,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
        val wallpaper = wallpaperState.wallpaper
        val viewSpec: WallpaperPreviewViewSpec = viewSpecFactory.wallpaperPreviewViewSpecWallpaperShowcase
        prefetchWallpaperPreviewToDisc(wallpaper, viewSpec, isUiSettled, visibleIndex)
    }

    private fun prefetchWallpaperPreviewFeedSingleToDisc(
        wallpaper: Wallpaper,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
        prefetchWallpaperPreviewToDisc(
            wallpaper = wallpaper,
            viewSpec = viewSpecFactory.wallpaperPreviewViewSpecFeedSingle,
            isUiSettled = isUiSettled,
            visibleIndex = visibleIndex,
        )
    }

    private fun prefetchWallpaperPreviewFeedTrackToDisc(
        wallpaper: Wallpaper,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
        prefetchWallpaperPreviewToDisc(
            wallpaper = wallpaper,
            viewSpec = viewSpecFactory.wallpaperPreviewViewSpecFeedTrackNoFooter,
            isUiSettled = isUiSettled,
            visibleIndex = visibleIndex,
        )
    }

    private fun prefetch(remixId: RemixId, isUiSettled: StateFlow<Boolean>, visibleIndex: Int) {
//        Log.d("data prefetch remixId: $remixId, visibleIndex: $visibleIndex")
        contentRepository.getWallpaperContent(remixId)
//            .onCompletion {
//                Log.d("data prefetch complete - remixId: $remixId, visibleIndex: $visibleIndex")
//            }
            .transformWhile { data ->
                emit(data)
                (data.artist == null || data.wallpaper == null).also { incomplete ->
                    if (!incomplete) {
                        data.wallpaperState?.also { wallpaperState ->
                            prefetchWallpaperPreviewHeroToDisc(wallpaperState, isUiSettled, visibleIndex)
                        }
                        data.artist?.also { artist ->
                            prefetchArtistPreviewHeroToDisc(artist, isUiSettled, visibleIndex)
                        }
                    }
                }
            }
            .collectIn(coroutineScopeIo) { }
    }

    private fun prefetch(
        collectionId: CollectionId,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
//        Log.d("prefetch collectionId: $collectionId")
        contentRepository.getCollectionState(collectionId)
//            .onCompletion {
//                Log.d("data prefetch complete - collectionId: $collectionId, visibleIndex: $visibleIndex")
//            }
            .transformWhile { data ->
                emit(data)
                (data == null).also { incomplete ->
                    if (!incomplete) {
                        // Note: if Collection sizes increase past the current soft cap of ~6,
                        // it's likely ideal to prefetch only the first X wallpaper previews rather
                        // than all.
                        data?.wallpapers?.forEach { wallpaper ->
                            prefetchWallpaperPreviewFeedTrackToDisc(
                                wallpaper = wallpaper,
                                isUiSettled = isUiSettled,
                                visibleIndex = visibleIndex,
                            )
                        }
                        data?.artist?.also { artist ->
                            prefetchArtistPreviewHeroToDisc(
                                artist,
                                isUiSettled,
                                visibleIndex,
                            )
                        }
                    }
                }
            }
            .collectIn(coroutineScopeIo) { }
    }

    private fun prefetch(
        folderId: FolderId,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
//        Log.d("prefetch folderId: $folderId")
        folderStateRepository.getFolderState(folderId)
//            .onCompletion {
//                Log.d("data prefetch complete - folderId: $folderId, visibleIndex: $visibleIndex")
//            }
            .transformWhile { data ->
                emit(data)

                data.wallpaperStates
                    .take(6)
                    .forEach { wallpaperState ->
                        if (wallpaperState.wallpaper.isSingle) {
                            prefetchWallpaperPreviewFeedSingleToDisc(
                                wallpaper = wallpaperState.wallpaper,
                                isUiSettled = isUiSettled,
                                visibleIndex = visibleIndex,
                            )
                        } else {
                            prefetchWallpaperPreviewFeedTrackToDisc(
                                wallpaper = wallpaperState.wallpaper,
                                isUiSettled = isUiSettled,
                                visibleIndex = visibleIndex
                            )
                        }
                    }

                // No longer required given the Folder UI does not show a profile picture
//                prefetchArtistPreviewHeroToDisc(
//                    artistId = data.folder.artistId,
//                    profileImageModel = data.folder.profileImageModel,
//                    isUiSettled = isUiSettled,
//                    visibleIndex = visibleIndex,
//                )
                false
            }
            .collectIn(coroutineScopeIo) { }
    }

    private fun prefetch(
        artistId: ArtistId,
        isUiSettled: StateFlow<Boolean>,
        visibleIndex: Int,
    ) {
        // Fetch the artist data using the repository
        artistRepository.getArtist(artistId)
            .transformWhile { artist ->
                emit(artist)
                (artist == null).also { incomplete ->
                    if (!incomplete) {
                        // If artist data is available, proceed with prefetching
                        // Prefetch artist preview or hero to disk
                        artist?.let {
                            prefetchArtistPreviewHeroToDisc(
                                artist = it,
                                isUiSettled = isUiSettled,
                                visibleIndex = visibleIndex
                            )
                        }
                    }
                }
            }
            .collectIn(coroutineScopeIo) { }
    }

    private fun prefetchArtistOnboarding(
        artistState: ArtistState,
        isUiSettled: StateFlow<Boolean>,
    ) {
        val artistViewState = viewStateMapper.mapArtistPreviewOnboarding(artistState)

        artistViewState.profileImage.imageViewState.also {
            prefetchImageViewStateToDisc(
                id = artistState.artist.id.name,
                imageViewState = it,
                isUiSettled = isUiSettled,
                logId = "${artistState.artist.id.name}-profile"
            )
        }

        artistViewState.backgroundImages.forEachIndexed { index, imageViewState ->
            prefetchImageViewStateToDisc(
                    id = artistState.artist.id.name,
                    imageViewState = imageViewState,
                    isUiSettled = isUiSettled,
                    logId = "${artistState.artist.id.name}-bg-$index"
                )
        }
    }

    private fun prefetchExplore(
        contentStates: List<ContentState>,
        isUiSettled: StateFlow<Boolean>,
        itemsToPrefetch: Int = 10,
    ) {
        contentStates
            .take(itemsToPrefetch)
            .forEachIndexed { index, contentState ->
            when (contentState) {
                is ContentState.Wallpaper -> {
                    prefetch(contentState.wallpaper.id, isUiSettled, index)
                }
                is ContentState.Collection -> {
                    prefetch(contentState.id, isUiSettled, index)
                }
                is ContentState.Highlights -> {
                    val highlights = contentState.highlights
                    prefetchCarouselHighlights(highlights)

                    // Prefetch Folder highlights
                    highlights.highlights
                        .filterIsInstance<FolderHighlight>()
                        .firstOrNull()
                        ?.also { folderHighlight ->
//                            Log.i("prefetchExplore(): Folder: ${folderHighlight.folder.id}")
                            prefetch(folderHighlight.folder.id, isUiSettled, index)
                        }

                    // Prefetch Artist highlights
                    contentState.highlights.highlights
                        .filterIsInstance<ArtistHighlight>()
                        .firstOrNull()
                        ?.also { artistHighlight ->
                            prefetch(artistHighlight.artist.id, isUiSettled, index)
                        }
                }
                else -> {}
            }
        }
    }

    override fun prefetch(id: Id, isUiSettled: StateFlow<Boolean>, visibleIndex: Int) {
        when (id) {
            is RemixId -> prefetch(id, isUiSettled, visibleIndex)
            is CollectionId -> prefetch(id, isUiSettled, visibleIndex)
            is FolderId -> prefetch(id, isUiSettled, visibleIndex)
            is ArtistId -> prefetch(id, isUiSettled, visibleIndex)
            else -> {}
        }
    }

    private fun prefetch(imageViewState: ImageViewState) {
        val id = imageViewState.image.imageUrl ?: return

        prefetchImageViewStateToDisc(
            id = id,
            imageViewState = imageViewState,
            isUiSettled = isUiSettledTrue,
            logId = id,
        )
    }

    override fun prefetchExplore() {
//        Log.d("data prefetch explore START")
        coroutineScopeIo.launch {
            waitForUiReady()

            val data = exploreRepository.contentStateFeed
//            .onCompletion {
//                Log.d("data prefetch complete - explore")
//            }
                .filterNotNull()
                .first()
//                        Log.d("data prefetch explore - images START - ${data.feed.size} items")
            prefetchExplore(data.feed, isUiSettledTrue, itemsToPrefetch = 10)
        }
    }

    override fun prefetchHomeOnboarding() {
//        Log.d("data prefetch home onboarding START")
        coroutineScopeIo.launch {
            waitForUiReady()

            val data = contentRepository.artistStates
//            .onCompletion {
//                Log.d("data prefetch complete - home onboarding")
//            }
                .filterNotNull()
                .first()
//                        Log.d("data prefetch home onboarding - images START - ${data.size} artists")
            data.forEach { artistState ->
                prefetchArtistOnboarding(artistState, isUiSettledTrue)
            }
        }
    }

    override fun prefetchHighlights(highlights: Highlights) {
        coroutineScopeIo.launch {
            waitForUiReady()
            highlights.highlights.forEach { highlight ->
                prefetchCarouselHighlight(highlight)
                when (highlight) {
                    is ArtistHighlight -> {
                        prefetch(id = highlight.artist.id, isUiSettledTrue, visibleIndex = 0)
                    }
                    is CollectionHighlight -> {
//                        Log.d("prefetchHighlights(): Collection: ${highlight.collectionId}")
                        prefetch(id = highlight.collectionId, isUiSettledTrue, visibleIndex = 0)
                    }
                    is FolderHighlight -> {
//                        Log.d("prefetchHighlights(): Folder: ${highlight.folder.id}")
                        prefetch(id = highlight.folder.id, isUiSettledTrue, visibleIndex = 0)
                    }
                    is PlusHighlight -> { }
                    SignInHighlight -> { }
                    is WallpaperHighlight -> {
                        prefetch(id = highlight.wallpaper.id, isUiSettledTrue, visibleIndex = 0)
                    }
                }
            }
        }
    }

    private fun prefetchCarouselHighlights(highlights: Highlights) {
        highlights.highlights.forEach { highlight ->
            prefetchCarouselHighlight(highlight)
        }
    }

    private fun prefetchCarouselHighlight(highlight: Highlight) {
        val carouselPageViewState = viewStateMapper.mapCarouselPage(
            highlight,
        )

        val viewState = carouselPageViewState.view.viewState as? ExhibitViewState ?: return
        val imageViewState = viewState.imageViewState
        val id = viewState.viewId?.id ?: return
        val logId = "Exhibit-${id}"

        prefetchImageViewStateToDisc(id, imageViewState, isUiSettledTrue, logId)
    }
}