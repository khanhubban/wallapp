package wallapp.view

import wallapp.content.model.Id
import wallapp.pixel.view.ViewId

interface ViewIdMapper {

    fun mapWallpaperPreviewViewId(id: Id): ViewId

    fun mapCollectionPreviewViewId(id: Id, secondaryId: String? = null): ViewId

    fun mapProfileCuratorViewId(id: Id): ViewId

    fun mapExhibitViewId(id: Id): ViewId

    fun unmap(viewId: ViewId): Any?
}