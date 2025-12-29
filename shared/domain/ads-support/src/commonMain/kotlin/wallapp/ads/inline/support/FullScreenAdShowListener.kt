package wallapp.ads.inline.support

import kotlinx.coroutines.flow.Flow
import wallapp.ads.AdType

/**
 * Exposes LiveData elements that can be subscribed to for global observing of any full screen ad.
 */
interface FullScreenAdShowListener {

    val adShowed: Flow<AdType>

    val adDismissed: Flow<AdType>

}
