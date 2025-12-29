package wallapp.search.model

import wallapp.content.model.Id.FolderId

data class SearchFolderMetadata(
    val folderId: FolderId,
    val folderNames: List<SearchFilterEntry>,
)
