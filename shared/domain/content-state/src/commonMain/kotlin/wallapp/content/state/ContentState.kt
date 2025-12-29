package wallapp.content.state

import wallapp.content.model.Id
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.data.collection.CollectionState
import wallapp.content.model.Wallpaper as WallpaperModel
import wallapp.data.highlight.Highlights as HighlightsModel

sealed interface ContentState {

    val id: Id?

    data class Wallpaper(val wallpaper: WallpaperModel): ContentState {
        override val id: Id
            get() = wallpaper.id
    }

    data class Collection(
        val collectionState: CollectionState,
        val viewSpec: CollectionPreviewViewSpec,
    ): ContentState {
        override val id: Id
            get() = collectionState.id
    }

    data class Highlights(val highlights: HighlightsModel): ContentState {
        override val id: Id?
            get() = null
    }
}