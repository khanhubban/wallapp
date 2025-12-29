package wallapp.data.content.model

import wallapp.content.model.Id
import wallapp.content.model.Id.Companion.ShortStringIdPrefixAritst
import wallapp.content.model.Id.Companion.ShortStringSeparator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class IdTest {

    @Test fun `to and from short string`() {
        val remixId = Id.RemixId("~remixname")
        assertEquals(remixId, Id.fromExportShortString(remixId.exportString))

        val designId = Id.DesignId("~design")
        assertEquals(designId, Id.fromExportShortString(designId.exportString))

        val categoryId = Id.CategoryId("~categoryname")
        assertEquals(categoryId, Id.fromExportShortString(categoryId.exportString))

        val artistId = Id.ArtistId("~artistname")
        assertEquals(artistId, Id.fromExportShortString(artistId.exportString))

        val collectionId = Id.CollectionId("~collectionname")
        assertEquals(collectionId, Id.fromExportShortString(collectionId.exportString))
    }

    @Test fun `invalid strings throw exceptions`() {
        assertFailsWith<IllegalArgumentException> {
            Id.fromExportShortString("")
        }
        assertFailsWith<IllegalArgumentException> {
            Id.fromExportShortString("id${ShortStringSeparator}fds")
        }
        assertFailsWith<IllegalArgumentException> {
            Id.fromExportShortString("$ShortStringIdPrefixAritst${ShortStringSeparator}one${ShortStringSeparator}")
        }
        assertFailsWith<IllegalArgumentException> {
            Id.fromExportShortString("one")
        }
    }
}