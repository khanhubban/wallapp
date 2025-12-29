package wallapp.ad

import kotlinx.coroutines.flow.StateFlow
import wallapp.manager.Manager
import wallapp.pixel.view.View

interface AdManager : Manager {

    val feedAdsEnabled: StateFlow<Boolean>

    fun createFeedAd(index: Int, source: AdSource = AdSource.Random, fullWidth: Boolean = true): View?

    fun resetRandomness()
}