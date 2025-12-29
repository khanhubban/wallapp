package wallapp.view

import androidx.compose.runtime.Immutable
import wallapp.content.model.Id
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId
import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.content.state.exhibit.ExhibitViewState
import wallapp.content.state.profile.ProfileCuratorViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.pixel.view.ViewId

@Immutable
class ViewIdMapperDefault : ViewIdMapper {

    companion object {
        const val WallpaperPreviewViewIdPrefix = WallpaperPreviewViewState.IdSuffix
        const val CollectionPreviewViewIdPrefix = CollectionPreviewViewState.IdSuffix
        const val ProfileCuratorViewIdPrefix = ProfileCuratorViewState.IdSuffix
        const val ExhibitViewIdPrefix = ExhibitViewState.IdSuffix
    }

    override fun mapWallpaperPreviewViewId(id: Id): ViewId {
        return ViewId("${id.name}${WallpaperPreviewViewIdPrefix}")
    }

    override fun mapCollectionPreviewViewId(id: Id, secondaryId: String?): ViewId {
        val secondarySuffix = if (secondaryId != null) "$secondaryId" else ""
        return ViewId("${id.name}${CollectionPreviewViewIdPrefix}$secondarySuffix")
    }

    override fun mapProfileCuratorViewId(id: Id): ViewId {
        return ViewId("${id.name}${ProfileCuratorViewIdPrefix}")
    }

    override fun mapExhibitViewId(id: Id): ViewId {
        return ViewId("${id.name}${ExhibitViewIdPrefix}")
    }

    override fun unmap(viewId: ViewId): Any? {
        val id = viewId.id
        return when {
            id.endsWith(WallpaperPreviewViewIdPrefix) -> {
                RemixId(id.removeSuffix(WallpaperPreviewViewIdPrefix))
            }
            id.contains(CollectionPreviewViewIdPrefix) -> {
                val index = id.indexOf(CollectionPreviewViewIdPrefix)
                CollectionId(id.substring(0, index))
            }
            id.endsWith(ProfileCuratorViewIdPrefix) -> {
                RemixId(id.removeSuffix(ProfileCuratorViewIdPrefix))
            }
            id.endsWith(ExhibitViewIdPrefix) -> {
                RemixId(id.removeSuffix(ExhibitViewIdPrefix))
            }
            else -> null
        }
    }
}