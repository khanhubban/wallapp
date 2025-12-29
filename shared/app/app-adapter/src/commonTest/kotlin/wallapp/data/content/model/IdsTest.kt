package wallapp.data.content.model

import wallapp.content.model.Id
import wallapp.content.model.Ids
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IdsTest {

//    @Test
//    fun `fromExportString legacy id`() {
//        val result = Ids.fromExportString("{\"type\":\"wallapp.content.model.Id.RemixId\",\"name\":\"a~artistname_dfe5ca64\"}")
//        assertNotNull(result)
//        assertEquals(1, result.ids.size)
//        assertEquals(result, Ids(listOf(RemixId("a~artistname_dfe5ca64"))))
//    }

    @Test
    fun `fromExportString returns null for empty string`() {
        val result = Ids.fromExportString("")
        assertNull(result)
    }

    @Test
    fun `fromExportString handles single ID without separator correctly`() {
        val testString = "id~a::ArtistName"
        val result = Ids.fromExportString(testString)
        assertNotNull(result)
        assertEquals(1, result.ids.size)
        assertTrue(result.ids.first() is Id.ArtistId)
        assertEquals("ArtistName", (result.ids.first() as? Id.ArtistId)?.name)
    }

    @Test
    fun `fromExportString handles multiple IDs with separator correctly`() {
        val testString = "id~a::ArtistName<,>id~d::DesignName"
        val result = Ids.fromExportString(testString)
        assertNotNull(result)
        assertEquals(2, result.ids.size)
        assertTrue(result.ids.any { it is Id.ArtistId && it.name == "ArtistName" })
        assertTrue(result.ids.any { it is Id.DesignId && it.name == "DesignName" })
    }

    @Test
    fun `fromExportString throws IllegalArgumentException for unknown prefix`() {
        assertFailsWith<IllegalArgumentException>() {
            val testString = "id~unknown::SomeName"
            Ids.fromExportString(testString)
        }
    }

    @Test
    fun `fromExportString handles complex case with multiple IDs correctly`() {
        // Adjust the test string to include a more complex scenario if needed
        val testString = "id~a::ArtistName1<,>id~cat::CategoryName<,>id~d::DesignName"
        val result = Ids.fromExportString(testString)
        assertNotNull(result)
        assertEquals(3, result.ids.size)
        // Further asserts can be added to validate each ID type and name
    }
}