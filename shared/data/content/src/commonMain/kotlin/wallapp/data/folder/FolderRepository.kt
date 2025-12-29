package wallapp.data.folder

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.FolderId

interface FolderRepository {

    val folders: Flow<List<Folder>>
    fun getFolder(id: FolderId): Flow<Folder?>

    val justAddedFolder: Flow<Folder?>
}