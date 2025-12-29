package wallapp.data.curator

import wallapp.content.model.Id
import wallapp.media.model.MediaHolder

/**
 * A base class to represent either a [wallapp.data.artist.Artist] or a
 * [wallapp.data.folder.Folder].
 */
interface Curator {

    val id: Id

    val title: String
    // Used on certain UI elements where the title is displayed on two lines.
    val titleTwoLines: String

    val profileImageMediaHolder: MediaHolder
}