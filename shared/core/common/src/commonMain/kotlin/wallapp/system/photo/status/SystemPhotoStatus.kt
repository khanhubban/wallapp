package wallapp.system.photo.status

import kotlinx.serialization.Serializable
import wallapp.system.photo.SystemPhotoId

@Serializable
sealed class SystemPhotoStatus {

    @Serializable
    data class ExistsInPhotoLibrary(val systemPhotoId: SystemPhotoId) : SystemPhotoStatus()

    @Serializable
    data object NotInPhotoLibrary : SystemPhotoStatus()

    @Serializable
    data object NotAuthorizedToCheck : SystemPhotoStatus()
}

val SystemPhotoStatus.existsInPhotoLibrary: Boolean
    get() = this is SystemPhotoStatus.ExistsInPhotoLibrary