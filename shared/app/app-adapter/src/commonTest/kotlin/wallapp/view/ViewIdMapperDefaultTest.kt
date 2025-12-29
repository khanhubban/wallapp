package wallapp.view

import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId
import kotlin.test.Test
import kotlin.test.assertEquals


class ViewIdMapperDefaultTest {

    @Test
    fun mapToAndFromRemixId() {
        val mapper = ViewIdMapperDefault()

        val remixId = RemixId("remixId")
        val viewId = mapper.mapWallpaperPreviewViewId(remixId)
        val id = mapper.unmap(viewId)

        assertEquals(remixId, id)
    }

    @Test
    fun mapToAndFromCollectionId() {
        val mapper = ViewIdMapperDefault()

        val collectionId = CollectionId("collectionId")
        val viewId = mapper.mapCollectionPreviewViewId(collectionId)
        val id = mapper.unmap(viewId)

        assertEquals(collectionId, id)
    }
}