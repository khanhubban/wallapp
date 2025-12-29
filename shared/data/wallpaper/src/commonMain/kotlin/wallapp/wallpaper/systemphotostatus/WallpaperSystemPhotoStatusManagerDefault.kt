package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import wallapp.appvisibility.AppVisibility
import wallapp.content.model.Id
import wallapp.coroutine.collectIn
import wallapp.system.photo.status.SystemPhotoStatus
import wallapp.system.photo.status.SystemPhotoStatusChecker

class WallpaperSystemPhotoStatusManagerDefault(
    private val systemPhotoStatusChecker: SystemPhotoStatusChecker,
    private val wallpaperSystemPhotoStatusRepository: WallpaperSystemPhotoStatusRepository,
    appVisibility: AppVisibility,
    coroutineScopeIo: CoroutineScope,
) : WallpaperSystemPhotoStatusManager {

    override suspend fun getSystemPhotoStatus(id: Id): SystemPhotoStatus =
        wallpaperSystemPhotoStatusRepository.getSystemPhotoStatus(id)

    override fun getSystemPhotoStatusFlow(id: Id) =
        wallpaperSystemPhotoStatusRepository.getSystemPhotoStatusFlow(id)

    override fun setSystemPhotoStatus(id: Id, status: SystemPhotoStatus) {
        wallpaperSystemPhotoStatusRepository.setSystemPhotoStatus(id, status)
    }

    private suspend fun refreshSystemPhotoStatus(id: Id) {
        val status = wallpaperSystemPhotoStatusRepository.getSystemPhotoStatus(id)

        if (status is SystemPhotoStatus.ExistsInPhotoLibrary) {
            val updatedStatus = systemPhotoStatusChecker
                .getSystemPhotoStatusSuspend(status.systemPhotoId)
            setSystemPhotoStatus(id, updatedStatus)
        }
    }

    private val currentPollId = MutableStateFlow<Id?>(null)
    override fun setPollSystemPhotoStatusForId(id: Id?) {
        currentPollId.value = id
    }

    init {
        combine(currentPollId, appVisibility.isVisible) { id, appIsVisible ->
            id to appIsVisible
        }.collectIn(coroutineScopeIo) { (id, appIsVisible) ->
            if (id != null && appIsVisible) {
                refreshSystemPhotoStatus(id)
            }
        }
    }
}