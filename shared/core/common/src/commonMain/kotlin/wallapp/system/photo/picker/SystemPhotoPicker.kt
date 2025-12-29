package wallapp.system.photo.picker


interface SystemPhotoPicker {

    val enabled: Boolean

    fun navigateToSystemPhotoPicker(onComplete: SystemPhotoPickerCallback)
}