package wallapp.wallpaper.saver

import platform.Foundation.NSError
import wallapp.system.photo.SystemPhotoId

sealed class SaveImageResult {
    data class Success(val localIdentifier: String?) : SaveImageResult() {
        val systemPhotoId: SystemPhotoId? by lazy { localIdentifier?.let { SystemPhotoId(it) } }
    }

    data class Error(val error: NSError, val message: String) : SaveImageResult()

    companion object {

        fun from(success: Boolean, localIdentifier: String?, error: NSError?): SaveImageResult {
            return if (success) {
                Success(localIdentifier)
            } else {
                Error(error ?: NSError(), error?.localizedDescription ?: "Unknown error")
            }
        }
    }
}

typealias SaveImageCallback = (SaveImageResult) -> Unit
