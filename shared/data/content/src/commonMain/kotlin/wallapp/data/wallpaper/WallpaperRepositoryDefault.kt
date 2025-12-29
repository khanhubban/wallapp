package wallapp.data.wallpaper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryType
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.content.model.remix
import wallapp.data.model.ModelRepository
import wallapp.log.Log


open class WallpaperRepositoryDefault(
    private val modelRepository: ModelRepository,
    coroutineScopeIo: CoroutineScope,
) : WallpaperRepositoryImpl() {

    override val refreshCounter = MutableStateFlow(0)

    override val remixes: Flow<List<WallpaperRemix>>
        get() = modelRepository.allRemixes

//    val designs: Flow<List<WallpaperDesign>>
//        get() = wallpaperDataRepository.allDesigns

    override val categories: Flow<List<WallpaperCategory>>
        get() = modelRepository.allCategories

    override val collectionCategories: Flow<List<WallpaperCategory>>
        get() = modelRepository.allCategories.mapNotNull { categories ->
            categories.filter { it.categoryType == WallpaperCategoryType.Collection }
        }

    override val allWallpaperItems: Flow<List<WallpaperItem>>
        get() = modelRepository.allWallpaperItems

    override val allWallpapers: Flow<List<Wallpaper>> by lazy {
        allWallpaperItems
            .map { all -> all.filterIsInstance<Wallpaper>() }
    }

    override val allSingles: Flow<List<Wallpaper>> by lazy {
        allWallpapers
            .map { all -> all.filter { it.remix.isSingle } }
    }

    override val allTracks: Flow<List<Wallpaper>> by lazy {
        allWallpapers
            .map { all -> all.filter { it.remix.isTrack } }
    }

    init {
        Log.d("WallpaperRepositoryDefault created")
        coroutineScopeIo.launch {
            allWallpaperItems.collect {
                Log.d("WallpaperRepositoryDefault allWallpaperItems(): size: ${it.size}")
            }
        }
    }
}