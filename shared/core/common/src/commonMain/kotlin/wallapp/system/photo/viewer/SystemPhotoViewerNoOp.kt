package wallapp.system.photo.viewer

import wallapp.system.photo.SystemPhotoId

object SystemPhotoViewerNoOp : SystemPhotoViewer {
    override fun openPhotoViewer(systemPhotoId: SystemPhotoId) {}
}