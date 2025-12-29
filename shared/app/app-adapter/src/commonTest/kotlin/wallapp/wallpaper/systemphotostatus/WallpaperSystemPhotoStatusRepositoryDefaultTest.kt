package wallapp.wallpaper.systemphotostatus

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import wallapp.content.model.Id
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionManagerMockAuthorized
import wallapp.permission.SystemPermissionManagerMockDenied
import wallapp.system.photo.SystemPhotoId
import wallapp.system.photo.status.SystemPhotoStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class WallpaperSystemPhotoStatusRepositoryDefaultTest {

    private val systemPermissionManagerAuthorized = SystemPermissionManagerMockAuthorized()
    private val systemPermissionManagerDenied = SystemPermissionManagerMockDenied()

    fun TestScope.WallpaperSystemPhotoStatusRepositoryDefault(
        systemPermissionManager: SystemPermissionManager = SystemPermissionManagerMockAuthorized()
    ): WallpaperSystemPhotoStatusRepositoryDefault = WallpaperSystemPhotoStatusRepositoryDefaultMock(
        coroutineScope = TestScope(),
        systemPermissionManager = systemPermissionManager
    )

    @Test
    fun addSystemPhotoStatus() = runTest {
        val repo = WallpaperSystemPhotoStatusRepositoryDefault(systemPermissionManagerAuthorized)
        val remixId = Id.RemixId("onetwothree")
        val systemPhotoId = SystemPhotoId("123")
        val systemPhotoStatus = SystemPhotoStatus.ExistsInPhotoLibrary(systemPhotoId)

        repo.setSystemPhotoStatus(remixId, systemPhotoStatus)
        assertEquals(systemPhotoStatus, repo.getSystemPhotoStatus(remixId))
    }

    @Test
    fun systemPhotoStatusIsUpdatedCorrectly() = runTest {
        val repo = WallpaperSystemPhotoStatusRepositoryDefault(systemPermissionManagerAuthorized)
        val remixId = Id.RemixId("updateCheck")
        val initialStatus = SystemPhotoStatus.ExistsInPhotoLibrary(SystemPhotoId("456"))
        val updatedStatus = SystemPhotoStatus.NotInPhotoLibrary

        repo.setSystemPhotoStatus(remixId, initialStatus)
        repo.setSystemPhotoStatus(remixId, updatedStatus)

        assertEquals(updatedStatus, repo.getSystemPhotoStatus(remixId))
    }

    @Test
    fun returnsNotAuthorizedToCheckWhenPermissionsDenied() = runTest {
        val repo = WallpaperSystemPhotoStatusRepositoryDefault(systemPermissionManagerDenied)
        val remixId = Id.RemixId("permissionsCheck")

        assertEquals(SystemPhotoStatus.NotAuthorizedToCheck, repo.getSystemPhotoStatus(remixId))
    }

    @Test
    fun returnsNotInPhotoLibraryForUnknownId() = runTest {
        val repo = WallpaperSystemPhotoStatusRepositoryDefault(systemPermissionManagerAuthorized)
        val unknownRemixId = Id.RemixId("unknown")

        assertEquals(SystemPhotoStatus.NotInPhotoLibrary, repo.getSystemPhotoStatus(unknownRemixId))
    }

    @Test
    fun multipleStatusesAreHandledCorrectly() = runTest {
        val repo = WallpaperSystemPhotoStatusRepositoryDefault(systemPermissionManagerAuthorized)
        val remixIdOne = Id.RemixId("first")
        val statusOne = SystemPhotoStatus.ExistsInPhotoLibrary(SystemPhotoId("789"))
        val remixIdTwo = Id.RemixId("second")
        val statusTwo = SystemPhotoStatus.NotInPhotoLibrary

        repo.setSystemPhotoStatus(remixIdOne, statusOne)
        repo.setSystemPhotoStatus(remixIdTwo, statusTwo)

        assertEquals(statusOne, repo.getSystemPhotoStatus(remixIdOne))
        assertEquals(statusTwo, repo.getSystemPhotoStatus(remixIdTwo))
    }
}