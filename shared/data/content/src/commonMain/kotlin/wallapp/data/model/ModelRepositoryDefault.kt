package wallapp.data.model

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.coroutine.CoroutineScopes
import wallapp.data.artist.Artist
import wallapp.data.folder.Folder
import wallapp.mediamap.MediaMapRepository
import wallapp.util.combine

class ModelRepositoryDefault(
    modelRepositoryRaw: ModelRepository,
    private val mediaMapRepository: MediaMapRepository,
    coroutineScopes: CoroutineScopes,
) : ModelRepository {

    private val isReady: StateFlow<Boolean>
        get() = mediaMapRepository.isReady

    override val allWallpaperItems: StateFlow<List<WallpaperItem>> =
        combine(isReady, modelRepositoryRaw.allWallpaperItems) { isReady, allWallpaperItems ->
            if (isReady) {
                allWallpaperItems
            } else {
                emptyList()
            }
        }.stateIn(coroutineScopes.main, started = SharingStarted.Eagerly, initialValue = emptyList())

    override val allRemixes: StateFlow<List<WallpaperRemix>> =
        combine(isReady, modelRepositoryRaw.allRemixes) { isReady, allRemixes ->
            if (isReady) {
                allRemixes
            } else {
                emptyList()
            }
        }.stateIn(coroutineScopes.main, started = SharingStarted.Eagerly, initialValue = emptyList())

    override val allCategories: StateFlow<List<WallpaperCategory>> =
        combine(isReady, modelRepositoryRaw.allCategories) { isReady, allCategories ->
            if (isReady) {
                allCategories
            } else {
                emptyList()
            }
        }.stateIn(coroutineScopes.main, started = SharingStarted.Eagerly, initialValue = emptyList())

    override val allArtists: StateFlow<List<Artist>> =
        combine(isReady, modelRepositoryRaw.allArtists) { isReady, allArtists ->
            if (isReady) {
                allArtists
            } else {
                emptyList()
            }
        }.stateIn(coroutineScopes.main, started = SharingStarted.Eagerly, initialValue = emptyList())

    override val allFolders: StateFlow<List<Folder>> =
        combine(isReady, modelRepositoryRaw.allFolders) { isReady, allFolders ->
            if (isReady) {
                allFolders
            } else {
                emptyList()
            }
        }.stateIn(coroutineScopes.main, started = SharingStarted.Eagerly, initialValue = emptyList())

}