package wallapp.ui.feed

import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerTopDefault
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerTopToolbar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FeedFormatterTest {

    @Test fun `compare default items`() {
        assertEquals(FeedSpacerTopDefault, FeedSpacerTopDefault)
        assertNotEquals(FeedSpacerTopDefault, FeedSpacerTopToolbar)
    }
}