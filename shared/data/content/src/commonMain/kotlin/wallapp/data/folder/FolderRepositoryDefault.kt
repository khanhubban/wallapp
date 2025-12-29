package wallapp.data.folder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wallapp.content.model.Id.FolderId

class FolderRepositoryDefault(
    private val folderRepositoryNetwork: FolderRepository,
) : FolderRepository {

    private val folderIdJustAdded: FolderId = FolderDefinitions.FolderIdJustAdded

    override val folders: Flow<List<Folder>>
        get() = folderRepositoryNetwork.folders

    override fun getFolder(id: FolderId): Flow<Folder?> {
        return folders.map { folders ->
            folders.find { it.id == id }
        }
    }

    override val justAddedFolder: Flow<Folder?> = folders.map { folder ->
        folder.find { it.id == folderIdJustAdded }
    }
}