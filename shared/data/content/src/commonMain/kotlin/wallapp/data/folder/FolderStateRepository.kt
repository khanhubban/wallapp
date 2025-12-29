package wallapp.data.folder

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.FolderId

interface FolderStateRepository {

    val folderStates: Flow<List<FolderState>>

    fun getFolderState(folderId: FolderId): Flow<FolderState>
}