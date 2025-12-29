package wallapp.data.folder

import wallapp.content.model.Id.FolderId
import wallapp.image.Image

data class FolderLink(
    val folderId: FolderId,
    val label: String,
    val icon: Image,
)
