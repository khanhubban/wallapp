package wallapp.content.model

import wallapp.content.model.Id.ArtistId

interface WallpaperItem {

    val id: Id
    val artistId: ArtistId
}
