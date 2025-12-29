package wallapp.ad

import wallapp.pixel.view.View

/**
 * Creates inline ads that appear in the feed.
 */
interface InternalAdFactory {

    fun createGetPlusAd(index: Int, fullWidth: Boolean): View
}