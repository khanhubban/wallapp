package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.flow.MutableStateFlow

interface WallpaperSystemPhotoStatusCacheDefaultData {

    val allCache: MutableStateFlow<String>
}