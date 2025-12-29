package wallapp.data.folder

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.FolderId
import wallapp.content.model.WallpaperId

interface FolderLinkRepository {

    fun getFolderLink(folderId: FolderId): Flow<FolderLink?>

    fun getFolderLinks(wallpaperId: WallpaperId): Flow<List<FolderLink>?>
}