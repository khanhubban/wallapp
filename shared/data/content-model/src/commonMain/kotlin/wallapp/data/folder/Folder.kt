package wallapp.data.folder

import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.FolderId
import wallapp.content.model.WallpaperId
import wallapp.data.curator.Curator
import wallapp.media.model.MediaHolder

data class Folder(
    override val id: FolderId,
    override val profileImageMediaHolder: MediaHolder,
    val featureBannerImageMediaHolder: MediaHolder,
    val wallpaperIds: List<WallpaperId>,
    val collectionIds: List<CollectionId>,
    override val title: String,
    override val titleTwoLines: String,
) : Curator

