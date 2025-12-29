package wallapp.appvisibility

import kotlinx.coroutines.flow.MutableStateFlow


/**
 * Wallpaper process [AppVisibility] implementation.
 */
class AppVisibilityWallpaperProcess : AppVisibility {

    override val isVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val visible: Boolean
        get() = isVisible.value

}