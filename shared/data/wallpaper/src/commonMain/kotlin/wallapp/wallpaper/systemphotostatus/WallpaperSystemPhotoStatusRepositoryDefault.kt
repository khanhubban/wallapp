package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import wallapp.content.model.Id
import wallapp.log.Logger
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionManagerMockAuthorized
import wallapp.permission.isAuthorizedOrLimited
import wallapp.system.photo.status.SystemPhotoStatus

@OptIn(InternalCoroutinesApi::class)
class WallpaperSystemPhotoStatusRepositoryDefault(
    private val systemPermissionManager: SystemPermissionManager,
    private val cache: WallpaperSystemPhotoStatusCache,
    private val coroutineScopeIo: CoroutineScope,
) : WallpaperSystemPhotoStatusRepository {

    companion object {
        val Log = Logger("WallpaperSystemPhotoStatusRepositoryDefault")
    }

    private val flowMap: MutableMap<Id, MutableStateFlow<SystemPhotoStatus>> = mutableMapOf()
    private val flowMapLock = SynchronizedObject()

    private val defaultSystemPhotoStatus: SystemPhotoStatus
        get() = if (!systemPermissionManager.systemMediaPermissionStatus.value.isAuthorizedOrLimited()) {
            SystemPhotoStatus.NotAuthorizedToCheck
        } else {
            SystemPhotoStatus.NotInPhotoLibrary
        }

    private fun update(id: Id, status: SystemPhotoStatus) {
        Log.d("update($id, $status)")
        getOrCreate(id, status).value = status
        cache.add(WallpaperSystemPhotoStatusCacheEntry(id, status))
    }

    private fun getOrCreate(
        id: Id,
        systemPhotoStatus: SystemPhotoStatus = defaultSystemPhotoStatus,
    ): MutableStateFlow<SystemPhotoStatus> {
        return synchronized(flowMapLock) {
            flowMap.getOrPut(id) {
                MutableStateFlow(systemPhotoStatus)
            }
        }
    }

    private fun getSystemPhotoStatusInternal(id: Id): StateFlow<SystemPhotoStatus> {
        return getOrCreate(id)
    }

    override suspend fun getSystemPhotoStatus(id: Id): SystemPhotoStatus {
        return getSystemPhotoStatusInternal(id).value.also {
            Log.d("getSystemPhotoStatus($id) = $it")
        }
    }

    override fun getSystemPhotoStatusFlow(id: Id): Flow<SystemPhotoStatus> =
        getSystemPhotoStatusInternal(id)

    override fun setSystemPhotoStatus(id: Id, status: SystemPhotoStatus) {
        update(id, status)
        Log.d("addSystemPhotoStatus($id, $status)")
    }

    init {
        coroutineScopeIo.launch {
            cache.all.collect { entries ->
                entries.forEach { entry ->
                    update(entry.id, entry.status)
                }
            }
        }
    }
}

fun WallpaperSystemPhotoStatusRepositoryDefaultMock(
    coroutineScope: CoroutineScope,
    systemPermissionManager: SystemPermissionManager = SystemPermissionManagerMockAuthorized(),
    cache: WallpaperSystemPhotoStatusCache = WallpaperSystemPhotoStatusCacheMemory(),
): WallpaperSystemPhotoStatusRepositoryDefault {
    return WallpaperSystemPhotoStatusRepositoryDefault(
        systemPermissionManager = systemPermissionManager,
        cache = cache,
        coroutineScopeIo = coroutineScope,
    )
}
