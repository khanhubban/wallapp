package wallapp.profileimage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.account.Account
import wallapp.account.AccountManager
import wallapp.image.Image
import wallapp.image.ImageModel
import wallapp.resources.image.ImageRepository
import wallapp.system.photo.picker.SystemPhotoPicker
import wallapp.system.photo.picker.SystemPhotoPickerResult
import wallapp.util.combine

open class ProfileImageManagerDefault(
    private val accountManager: AccountManager,
    private val systemPhotoPicker: SystemPhotoPicker,
    private val imageRepository: ImageRepository,
    coroutineScopeMain: CoroutineScope,
) : ProfileImageManager {

    protected val fallbackProfileImage: Image
        get() = imageRepository.profile

    protected val signedInAccount: Flow<Account?>
        get() = accountManager.signedInAccount
    protected val accountProfileImage: Flow<Image?> = signedInAccount
        .map { account -> account?.photoUrl
            ?.let { Image.from(ImageModel.from(it)) }
        }

    protected val profileImageCacheId: Flow<String?> = signedInAccount.map { account ->
        account?.let { "profile_image_${it.userId}" }
    }

    protected val customProfileImage: MutableStateFlow<Image?> = MutableStateFlow(null)

    private fun arbitratedProfileImage(customProfileImage: Image?, accountProfileImage: Image?): Image {
        if (customProfileImage != null && customProfileImage != fallbackProfileImage) {
            return customProfileImage
        }
        return accountProfileImage ?: fallbackProfileImage
    }

    override val profileImage: StateFlow<Image> by lazy {
        combine(
            customProfileImage,
            accountProfileImage,
        ) { customProfileImage, accountProfileImage ->
            arbitratedProfileImage(customProfileImage, accountProfileImage)
        }.stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, fallbackProfileImage)
    }

    override fun navigateToProfileImagePicker() {
        if (!systemPhotoPicker.enabled) return

        systemPhotoPicker.navigateToSystemPhotoPicker { result ->
            if (result is SystemPhotoPickerResult.Success) {
                saveProfileImage(result.data)
            }
        }
    }

    open fun saveProfileImage(bitmapData: ByteArray) { }

    companion object {
        const val MaxSize = 512
    }
}