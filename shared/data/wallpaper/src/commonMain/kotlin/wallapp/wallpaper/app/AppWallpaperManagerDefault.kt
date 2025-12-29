package wallapp.wallpaper.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import wallapp.content.model.Id
import wallapp.content.model.WallpaperRemix
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.log.Log
import wallapp.preferences.UserPreferences

open class AppWallpaperManagerDefault(
    private val userPreferences: UserPreferences,
    private val wallpaperRepository: WallpaperRepository,
    private val coroutineScopeIo: CoroutineScope,
) : AppWallpaperManager {

//    override val blur: Boolean = true
//    override val animateBlur: Boolean = true

    override val currentDisplayWallpaper: MutableStateFlow<WallpaperRemix?> = MutableStateFlow(null)

    override fun updateCurrentDisplayWallpaper(remixId: Id.RemixId) {
        Log.i("[wallpaperId] updateCurrentDisplayWallpaper(): id: $remixId")
        userPreferences.currentPreviewRemixId.updateIfNew(remixId.name)
        updateWallpaperPreview(remixId)
    }

    private fun updateWallpaperPreview(remixId: Id.RemixId) {
        coroutineScopeIo.launch {
            wallpaperRepository.getRemix(remixId).collect {
                currentDisplayWallpaper.value = it
            }
        }
    }

    override fun onConfigurationChanged() { }

    init {
        userPreferences.currentPreviewRemixId.subscribe(skipFirst = false) { id ->
            updateWallpaperPreview(Id.RemixId(id))
        }
    }
}
