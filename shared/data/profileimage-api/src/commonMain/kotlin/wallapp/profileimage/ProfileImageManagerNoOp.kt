package wallapp.profileimage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.image.Image

object ProfileImageManagerNoOp : ProfileImageManager {

    override val profileImage: StateFlow<Image> = MutableStateFlow(Image.Preset)

    override fun navigateToProfileImagePicker() = Unit
}