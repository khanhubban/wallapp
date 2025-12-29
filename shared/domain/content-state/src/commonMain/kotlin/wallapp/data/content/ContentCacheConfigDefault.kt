package wallapp.data.content

import kotlinx.coroutines.flow.StateFlow
import wallapp.app.AppStateManager
import wallapp.network.NetworkState

class ContentCacheConfigDefault(
    private val appStateManager: AppStateManager,
    private val networkState: NetworkState,
) : ContentCacheConfig {
    
    override val isUiReady: StateFlow<Boolean>
        get() = appStateManager.isUiReady

    override val prefetchWallpaperImages: Boolean
        get() = networkState.isConnected
}