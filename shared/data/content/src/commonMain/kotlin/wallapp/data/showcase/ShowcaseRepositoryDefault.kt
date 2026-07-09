package wallapp.data.showcase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryType
import wallapp.content.model.WallpaperRemix
import wallapp.coroutine.CoroutineScopeMain
import wallapp.data.artist.ArtistRepository
import wallapp.data.content.media.ContentMediaRepository
import wallapp.data.content.useLightTopControls
import wallapp.data.folder.FolderDefinitions
import wallapp.data.folder.FolderRepository
import wallapp.data.folder.FolderStateRepository
import wallapp.data.highlight.Highlight
import wallapp.data.highlight.Highlight.ArtistHighlight
import wallapp.data.highlight.Highlight.CollectionHighlight
import wallapp.data.highlight.Highlight.FolderHighlight
import wallapp.data.highlight.Highlight.PlusHighlight
import wallapp.data.highlight.Highlight.SignInHighlight
import wallapp.data.highlight.Highlight.WallpaperHighlight
import wallapp.data.highlight.Highlights
import wallapp.data.highlight.debugString
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.log.Log
import wallapp.random.RandomManager
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.string.splitIntoLines
import wallapp.util.combine

class ShowcaseRepositoryDefault(
    private val showcaseRepositoryHighlightsConfig: ShowcaseRepositoryHighlightsConfig,
    wallpaperRepository: WallpaperRepository,
    folderRepository: FolderRepository,
    artistRepository: ArtistRepository,
    folderStateRepository: FolderStateRepository,
    private val contentMediaRepository: ContentMediaRepository,
    private val strings: StringRepository,
    private val randomManager: RandomManager,
    private val imageRepository: ImageRepository,
    @CoroutineScopeMain private val coroutineScopeMain: CoroutineScope,
) : ShowcaseRepository {

    private val deterministicRandom
        get() = randomManager.deterministicRandom

    private val justAddedCategoryId: StateFlow<CategoryId>
        get() = showcaseRepositoryHighlightsConfig.highlightJustAdded
    private val collectionOfTheWeekCategoryId: StateFlow<CategoryId>
        get() = showcaseRepositoryHighlightsConfig.highlightCollectionOfTheWeek
    private val mostPopularCategoryId: StateFlow<CategoryId>
        get() = showcaseRepositoryHighlightsConfig.highlightMostPopular
    private val highlightArtistId: StateFlow<ArtistId>
        get() = showcaseRepositoryHighlightsConfig.highlightArtist
    private val wallpaperOfTheWeekId: StateFlow<RemixId>
        get() = showcaseRepositoryHighlightsConfig.highlightWallpaperOfTheWeek
    private val showPlus: StateFlow<Boolean>
        get() = showcaseRepositoryHighlightsConfig.showPlus

    private val justAddedFolderWallpapers: Flow<List<Wallpaper>> =
        folderStateRepository.getFolderState(FolderDefinitions.FolderIdJustAdded)
            .map { folderState ->
                folderState.wallpaperStates.map { it.wallpaper }
            }
            .stateIn(coroutineScopeMain, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())

    override val signUpWallpapers: Flow<List<WallpaperRemix>> =
        justAddedFolderWallpapers.map {
            it.shuffled(deterministicRandom)
                .take(5)
        }
        .stateIn(coroutineScopeMain, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())

    private val fixedHighlights: Flow<List<Highlight>?> = MutableStateFlow(
        listOf(
            SignInHighlight,
        )
    )

    private val categoryHighlights: Flow<List<Highlight>?> =
        combine(
            collectionOfTheWeekCategoryId,
            wallpaperRepository.categories,
        ) { collectionOfTheWeek, wallpaperCategories ->

            val collectionOfTheWeekHighlight = wallpaperCategories.findHighlight(
                id = collectionOfTheWeek,
                label = strings.collectionOfTheWeek.splitIntoLines(2),
            )

            listOfNotNull(
                collectionOfTheWeekHighlight,
            ).ifEmpty { null }
        }.stateIn(coroutineScopeMain, SharingStarted.WhileSubscribed(), null)

    private fun List<WallpaperCategory>.findHighlight(
        id: Id,
        label: String,
    ): Highlight? {
        // [id] comes from Remote Config and may name a category of any type, or none at all.
        // CollectionHighlight requires a Collection, so filter on the type here: a mismatched
        // id must degrade to no highlight rather than tripping that require() and crashing.
        return find { it.id == id && it.categoryType == WallpaperCategoryType.Collection }
            ?.let {
                CollectionHighlight(
                    category = it,
                    previewWallpaper = it.previewRemix,
                    label,
                )
            }
    }

    private val artistHighlights: Flow<List<Highlight>?> =
        combine(
            highlightArtistId,
            artistRepository.artists,
        ) { highlightArtistId, artists ->
            artists.find { it.id == highlightArtistId }
                ?.let {
                    listOf(
                        ArtistHighlight(
                            artist = it,
                            label = strings.featuredArtist,
                        )
                    )
                }
        }

    private val wallpaperHighlights: Flow<List<Highlight>?> =
        combine(
            wallpaperOfTheWeekId,
            wallpaperRepository.allWallpapers
        ) { wallpaperOfTheWeekId, wallpapers ->
            wallpapers.find { it.id == wallpaperOfTheWeekId }
                ?.let {
                    listOf(
                        WallpaperHighlight(
                            wallpaper = it,
                            label = strings.wallpaperOfTheWeek.splitIntoLines(2),
                            useDarkStatusBarIcons = !it.topColorShade.useLightTopControls,
                        )
                    )
                }
        }

    private val folderHighlights: Flow<List<Highlight>?> =
        folderRepository.folders.map { folders ->
            folders
                .filter { it.id == FolderDefinitions.FolderIdJustAdded }
                .map { folder ->
                    FolderHighlight(
                        folder,
                        label = folder.titleTwoLines,
                    )
            }
        }

    private val plusHighlights: Flow<List<Highlight>> =
        showPlus.map { showPlus ->
            if (!showPlus) {
                listOf(
                    PlusHighlight(
                        upgradePlusImage = imageRepository.plusHighlightBackground,
                        label = strings.upgradeToPlus.splitIntoLines(2)
                    )
                )
            } else {
                emptyList()
            }
        }

    override val exploreHighlights: Flow<Highlights?> by lazy {
        combine(
            categoryHighlights,
            folderHighlights,
            artistHighlights,
            wallpaperHighlights,
            plusHighlights,
        ) { categoryHighlights, folderHighlights, artistHighlights, wallpaperHighlights, plusHighlights ->
            // iOS's carousel rendering has a render bug changing the number of items can cause
            // items to be rendering in the wrong order. Work around this by only returning valid
            // highlights for all supported content types are available.
            if (folderHighlights != null
                && wallpaperHighlights != null
                && categoryHighlights != null
                && artistHighlights != null) {
                val highlights = folderHighlights + artistHighlights + wallpaperHighlights + categoryHighlights + plusHighlights
                Highlights(
                    highlights,
                    startIndex = folderHighlights.firstOrNull()?.let { highlights.indexOf(it) } ?: 2,
                )
            } else {
                null
            }
        }
            .onEach { highlights: Highlights? ->
                Log.d("[Showcase]: exploreHighlights: size: ${highlights?.highlights?.size ?: 0}, " +
                        "${highlights?.highlights?.map { it.debugString } ?: emptyList() }")
            }
            .stateIn(coroutineScopeMain, started = SharingStarted.WhileSubscribed(), initialValue = null)
    }
}