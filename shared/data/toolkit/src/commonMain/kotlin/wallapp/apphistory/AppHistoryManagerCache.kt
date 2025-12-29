package wallapp.apphistory

import kotlinx.coroutines.flow.MutableStateFlow

interface AppHistoryManagerCache {
    val recentlyViewedWallpaperIds: MutableStateFlow<String>
    val recentlyViewedCollectionIds: MutableStateFlow<String>
    val recentlyViewedArtistIds: MutableStateFlow<String>
}