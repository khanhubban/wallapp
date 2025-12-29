package wallapp.data.folder

import wallapp.content.model.Id.FolderId
import wallapp.data.collection.CollectionState
import wallapp.data.wallpaper.WallpaperState

data class FolderState(
    val folder: Folder,
    val collectionStates: List<CollectionState>,
    val wallpaperStates: List<WallpaperState>,
    val showFlair: Boolean,
) {
    val id: FolderId
        get() = folder.id
}