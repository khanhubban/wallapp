package wallapp.media.network.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NetworkMediaDataTest {

    @Test
    fun testParseJson() {
        val jsonString = """
        {
    "version": 1,
    "data": {
        "1": {
            "dhd": "https://storage.googleapis.com/example-content/content/a~artistname_1b2aa4d6/Snowy~Mountain~A.jpg",
            "dsd": "https://storage.googleapis.com/example-content/content/a~artistname_1b2aa4d6/Snowy~Mountain~A.jpg?w=1080&h=1920&fit=crop&crop=faces,center&fm=png"
        },
        "2": {
            "ws": "https://storage.googleapis.com/example-content/content/a~artistname_cd821e68/a~artistname_cd821e68_preview.png?w=600&h=652&fit=crop&crop=faces,center&fm=avif",
            "wfs": "https://storage.googleapis.com/example-content/content/a~artistname_cd821e68/a~artistname_cd821e68_preview.png?w=260&h=411&fit=crop&crop=faces,center&fm=avif"
        },
        "3": {
            "dhd": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/AdamM~Pixelized~Key1.jpeg"
        },
        "4": {
            "ws": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=600&h=652&fit=crop&crop=faces,center&fm=avif",
            "wft": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=260&h=274&fit=crop&crop=faces,center&fm=avif",
            "wcl0": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=547&h=348&fit=crop&crop=faces,center&fm=avif",
            "wcl1": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=460&h=42&fit=crop&crop=faces,center&fm=avif",
            "wcl2": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=367&h=28&fit=crop&crop=faces,center&fm=avif",
            "wcs0": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=260&h=155&fit=crop&crop=faces,center&fm=avif",
            "wcs1": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=227&h=28&fit=crop&crop=faces,center&fm=avif",
            "wcs2": "https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=193&h=23&fit=crop&crop=faces,center&fm=avif"
        }
    }
}
        """
        val result = NetworkMediaData.fromJson(jsonString)

        assertEquals(1, result.version)

        val map = result.mediaMap
        assertEquals(4, map.size)
        assertTrue(map.containsKey(1))
        assertTrue(map.containsKey(2))
        assertTrue(map.containsKey(3))
        assertTrue(map.containsKey(4))

        val itemDataMap: Map<String, String>? = map[4]
        assertNotNull(itemDataMap)
        assertEquals(8, itemDataMap?.size)

        assertEquals("https://storage.googleapis.com/example-content/content/a~artistname_2e3466c3/a~artistname_2e3466c3_preview.png?w=600&h=652&fit=crop&crop=faces,center&fm=avif",
            itemDataMap["ws"])
    }

    @Test
    fun testParseEmptyJson() {
        val jsonString = """{}"""
        val result = NetworkMediaData.fromJson(jsonString)

        assertTrue(result.mediaMap.isEmpty())
    }

    @Test
    fun testParseInvalidJson() {
        val jsonString = """{"mediaMap": {"invalid_key": "https://example.com/image.jpg"}}"""
        val result = NetworkMediaData.fromJson(jsonString)
        assertTrue(result.mediaMap.isEmpty())
    }
}