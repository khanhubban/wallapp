package wallapp.profileimage

import kotlinx.coroutines.flow.StateFlow
import wallapp.image.Image

interface ProfileImageManager {

    val profileImage: StateFlow<Image>

    fun navigateToProfileImagePicker()
}