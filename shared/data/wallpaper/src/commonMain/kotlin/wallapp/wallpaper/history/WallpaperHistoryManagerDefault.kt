package wallapp.wallpaper.history

import wallapp.appstate.AppState
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.preference.MutableObservableValue

class WallpaperHistoryManagerDefault(
    val appState: AppState,
) : WallpaperHistoryManager {

    private val _wallpaperHistoryStack = mutableListOf<OrderedWallpaper>()
    override val wallpaperHistoryStack: List<OrderedWallpaper>
        get() = _wallpaperHistoryStack

    private var currentWallpaperId: OrderedWallpaper? = null

    private val wallpaperHistory: MutableObservableValue<String>
        get() = appState.wallpaperHistory

    init {
        _wallpaperHistoryStack.addAll(
            OrderedWallpapers.fromExportString(wallpaperHistory.value)?.wallpapers ?: emptySet()
        )
    }

    override fun onWallpaperChanged(value: WallpaperRemix) {
        val orderedWallpaper = OrderedWallpaper(
            value.id,
            _wallpaperHistoryStack.size
        )
        currentWallpaperId?.let {
            if (it.remixId != value.id) {
                _wallpaperHistoryStack.add(orderedWallpaper)
                updateHistoryStackInPreferences()
            }
        }
        currentWallpaperId = orderedWallpaper
    }

    override fun popPreviousRemix(): RemixId? {
        val currentWallpaper = currentWallpaperId ?: return null
        return _wallpaperHistoryStack.findLast {
            it.remixId != currentWallpaper.remixId
        }?.also {
            _wallpaperHistoryStack.remove(it)
            updateHistoryStackInPreferences()
        }?.remixId
    }

    override fun popPreviousWallpaper(): RemixId? {
        val currentWallpaper = currentWallpaperId ?: return null
        return _wallpaperHistoryStack.findLast {
            it.remixId != currentWallpaper.remixId
        }?.also {
            _wallpaperHistoryStack.remove(it)
            updateHistoryStackInPreferences()
        }?.remixId
    }

    override fun popPreviousRemixOrWallpaper(): RemixId? {
        val currentWallpaper = currentWallpaperId ?: return null
        return _wallpaperHistoryStack.findLast {
            it.remixId != currentWallpaper.remixId
        }?.also {
            _wallpaperHistoryStack.remove(it)
            updateHistoryStackInPreferences()
        }?.remixId
    }

    override fun popCurrent(): RemixId? {
        val currentWallpaper = currentWallpaperId ?: return null
        return _wallpaperHistoryStack.findLast {
            it.remixId == currentWallpaper.remixId
        }?.also {
            _wallpaperHistoryStack.remove(it)
            updateHistoryStackInPreferences()
            currentWallpaperId = _wallpaperHistoryStack.lastOrNull()
        }?.remixId
    }

    override fun clearHistory() {
        _wallpaperHistoryStack.clear()
        updateHistoryStackInPreferences()
    }

    private fun updateHistoryStackInPreferences() {
        wallpaperHistory.updateIfNew(
            OrderedWallpapers(_wallpaperHistoryStack.toSet()).exportString
        )
    }
}

