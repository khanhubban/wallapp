package wallapp.data.content

import kotlinx.coroutines.flow.StateFlow

interface ContentCacheConfig {

    val isUiReady: StateFlow<Boolean>

    val prefetchWallpaperImages: Boolean
}
