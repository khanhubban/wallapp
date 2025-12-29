package wallapp.data.folder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wallapp.content.model.Id.FolderId
import wallapp.content.model.WallpaperId
import wallapp.image.Image

class FolderLinkRepositoryDefault(
    private val folderRepository: FolderRepository,
) : FolderLinkRepository {

    private val folderIconMap: Map<FolderId, Pair<Image, String>> by lazy {
        mapOf()
    }

    private fun Folder?.toFolderLink(): FolderLink? {
        return if (this != null) {
            val data = folderIconMap[id] ?: return null
            FolderLink(id, icon = data.first, label = data.second)
        } else {
            null
        }
    }

    private val folders: Flow<List<Folder>>
        get() = folderRepository.folders

    override fun getFolderLink(folderId: FolderId): Flow<FolderLink?> =
        folders.map { allFolders ->
            val folder = allFolders.find { it.id == folderId }
            folder.toFolderLink()
        }

    override fun getFolderLinks(wallpaperId: WallpaperId): Flow<List<FolderLink>?> =
        folders.map { allFolders ->
            allFolders.filter { it.wallpaperIds.contains(wallpaperId) }
                .mapNotNull { it.toFolderLink() }
                .ifEmpty { null }
        }
}