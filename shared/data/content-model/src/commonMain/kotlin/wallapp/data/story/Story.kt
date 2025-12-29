package wallapp.data.story

import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.StoryId
import wallapp.content.model.Wallpaper
import wallapp.data.artist.Artist

data class Story(
    val id: StoryId,
    val artist: Artist,
    val wallpaper: Wallpaper?,
) {
    val artistId: ArtistId
        get() = artist.id
//    val wallpaperId: Id?
//        get() = wallpaperItem.id

}
