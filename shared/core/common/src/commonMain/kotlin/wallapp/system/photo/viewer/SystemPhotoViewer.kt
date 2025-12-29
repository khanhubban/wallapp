package wallapp.system.photo.viewer

import wallapp.system.photo.SystemPhotoId

interface SystemPhotoViewer {
    fun openPhotoViewer(systemPhotoId: SystemPhotoId)
}