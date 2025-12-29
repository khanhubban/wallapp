package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.flow.MutableStateFlow

class WallpaperSystemPhotoStatusCacheDefaultDataMock(
    override val allCache: MutableStateFlow<String> = MutableStateFlow(""),
) : WallpaperSystemPhotoStatusCacheDefaultData {

    constructor(allCache: String) : this(MutableStateFlow(allCache))
}
