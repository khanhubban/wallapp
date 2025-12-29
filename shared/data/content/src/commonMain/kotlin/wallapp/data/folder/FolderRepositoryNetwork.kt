package wallapp.data.folder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.Id.FolderId
import wallapp.data.model.ModelRepository

class FolderRepositoryNetwork(
    modelRepository: ModelRepository,
    coroutineScopeMain: CoroutineScope,
) : FolderRepository {

    override val folders: StateFlow<List<Folder>> = modelRepository.allFolders
        .map { allFolders -> allFolders.sortedBy { it.id.name } }
        .stateIn(coroutineScopeMain, SharingStarted.Eagerly, emptyList())

    override fun getFolder(id: FolderId): Flow<Folder?> {
        return folders.map { folders ->
            folders.find { it.id == id }
        }
    }

    override val justAddedFolder: Flow<Folder?> = folders.map { folder ->
        folder.find { it.id == FolderDefinitions.FolderIdJustAdded }
    }
}