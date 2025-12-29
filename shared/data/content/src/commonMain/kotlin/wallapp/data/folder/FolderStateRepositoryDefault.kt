package wallapp.data.folder

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.FolderId
import wallapp.content.model.WallpaperId
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentRepository
import wallapp.data.wallpaper.WallpaperState
import wallapp.data.wallpaper.WallpaperStateRepository
import wallapp.di.Lazy
import wallapp.random.RandomManager


class FolderStateRepositoryDefault(
    private val folderRepository: FolderRepository,
    contentRepository: Lazy<ContentRepository>,
    private val wallpaperStateRepository: WallpaperStateRepository,
    private val randomManager: RandomManager,
) : FolderStateRepository {

    private val contentRepository: ContentRepository by lazy { contentRepository.get() }

    private val deterministicRandom
        get() = randomManager.deterministicRandom

    fun getFolder(id: FolderId): Flow<Folder?> {
        return folderRepository.getFolder(id)
    }

    private fun getWallpaperState(wallpaperId: WallpaperId): Flow<WallpaperState?> {
        return wallpaperStateRepository.getWallpaperState(wallpaperId)
    }

    private fun getCollectionState(collectionId: CollectionId): Flow<CollectionState?> {
        return contentRepository.getCollectionState(collectionId)
    }

    private fun getFolderState(folder: Folder): Flow<FolderState> {
        val wallpaperIds = folder.wallpaperIds
        val wallpaperStatesFlow: Flow<List<WallpaperState>> = if (wallpaperIds.isNotEmpty()) {
            combine(wallpaperIds.map { wallpaperId -> getWallpaperState(wallpaperId) }) {
                it.filterNotNull()
            }
        } else {
            flowOf(emptyList())
        }

        val collectionIds = folder.collectionIds
        val collectionStatesFlow = if (collectionIds.isNotEmpty()) {
            combine(collectionIds.map { collectionId -> getCollectionState(collectionId) }) {
                it.filterNotNull()
            }
        } else {
            flowOf(emptyList())
        }

        return combine(wallpaperStatesFlow, collectionStatesFlow) { wallpaperStates, collectionStates ->
//            val sortedWallpapers = wallpaperStates
//                .shuffled(deterministicRandom)
            FolderState(
                folder = folder,
                collectionStates = collectionStates,
                wallpaperStates = wallpaperStates,
                showFlair = folder.id == FolderDefinitions.FolderIdJustAdded,
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFolderState(folderId: FolderId): Flow<FolderState> {
        return getFolder(folderId)
            .filterNotNull()
            .flatMapLatest { folder ->
            getFolderState(folder)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val folderStates: Flow<List<FolderState>>
        get() = folderRepository.folders
            .flatMapLatest { folders ->
                val folderStatesFlow = folders.map { getFolderState(it.id) }
                combine(folderStatesFlow) { folderStates -> folderStates.toList() }
            }
}