package wallapp.wallpaper.download

import kotlinx.coroutines.flow.StateFlow
import wallapp.account.data.AccountDataRepository
import wallapp.content.model.Id
import wallapp.content.model.WallpaperDownloadEvent
import wallapp.log.Log
import wallapp.time.TimeRepository

class WallpaperDownloadEventManagerDefault(
    private val timeRepository: TimeRepository,
    private val accountDataRepository: AccountDataRepository,
) : WallpaperDownloadEventManager {
    private val currentTime: Long
        get() = timeRepository.currentTime

    private val wallpaperDownloadEvents: StateFlow<List<WallpaperDownloadEvent>?> by lazy { accountDataRepository.wallpaperDownloadEvents }
    override suspend fun registerWallpaperDownloadEvent(id: Id.RemixId) {

        Log.e("[firestore] WallpaperDownloadEventManagerDefault.registerWallpaperDownloadEvent id=$id")

        val wallpaperDownloadEventsMutableList =
            (wallpaperDownloadEvents.value ?: emptyList()).toMutableList()

        val existingWallpaperDownloadEvent = wallpaperDownloadEventsMutableList.find {
            it.wallpaperId == id
        }

        // If wallpaperDownloadEvent already exists, first remove it and then add a new one.
        // If you do not remove it and use union, another wallpaperDownloadEvent with the same id will be created.
        if (existingWallpaperDownloadEvent != null) {

            existingWallpaperDownloadEvent.copy().apply {
                accountDataRepository.updateWallpaperDownloadEvent(this, false)
            }
            existingWallpaperDownloadEvent.times.add(currentTime).apply {
                accountDataRepository.updateWallpaperDownloadEvent(
                    existingWallpaperDownloadEvent,
                    true
                )
            }
        } else {
            accountDataRepository.updateWallpaperDownloadEvent(
                wallpaperDownloadEvent = WallpaperDownloadEvent(
                    id = id.exportString,
                    times = mutableListOf(currentTime),
                ),
                isAdd = true,
            )
        }

    }
}