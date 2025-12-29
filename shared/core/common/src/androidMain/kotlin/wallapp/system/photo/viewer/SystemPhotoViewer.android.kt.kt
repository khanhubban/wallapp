package wallapp.system.photo.viewer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import wallapp.system.photo.SystemPhotoId
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.activity

class SystemPhotoViewerAndroid(private val uiControllerManager: UiControllerManager) :
    SystemPhotoViewer {

    private val activity: Activity?
        get() = uiControllerManager.currentUiController?.activity

    override fun openPhotoViewer(systemPhotoId: SystemPhotoId) {
        val photoUri: Uri = Uri.withAppendedPath(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            systemPhotoId.id
        )

        // Create an Intent to open photo
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(photoUri, "image/*")
            // Add flags to grant permissions
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        activity?.startActivity(intent)
    }

}