package wallapp.system.photo.picker

object SystemPhotoPickerNoOp : SystemPhotoPicker {

    override val enabled: Boolean
        get() = false

    override fun navigateToSystemPhotoPicker(onComplete: SystemPhotoPickerCallback) { }
}