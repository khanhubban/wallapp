package wallapp.profileimage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import wallapp.account.AccountManager
import wallapp.coroutine.collectIn
import wallapp.image.Image
import wallapp.log.Log
import wallapp.resources.image.ImageRepository
import wallapp.system.photo.picker.SystemPhotoPicker
import wallapp.wallpaper.cache.BaseCacheSuspend

class ProfileImageManagerIos(
    accountManager: AccountManager,
    systemPhotoPicker: SystemPhotoPicker,
    imageRepository: ImageRepository,
    private val baseCache: BaseCacheSuspend,
    coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : ProfileImageManagerDefault(accountManager, systemPhotoPicker, imageRepository, coroutineScopeMain) {

    private fun setCurrentProfileImage(byteArray: ByteArray?) {
        Log.d("[ProfileImageManagerIos] setCurrentProfileImage()")
        customProfileImage.value = byteArray?.let { Image.from(byteArray) }
    }

    override fun saveProfileImage(bitmapData: ByteArray) {
        coroutineScopeIo.launch {
            val profileImageCacheId = profileImageCacheId.first() ?: return@launch
            Log.d("[ProfileImageManagerIos] saveProfileImage() - profileImageCacheId: $profileImageCacheId")
            setCurrentProfileImage(bitmapData)
            val saveResult = baseCache.putData(profileImageCacheId, bitmapData)
            Log.d("[ProfileImageManagerIos] saveProfileImage() - saveResult: $saveResult")
        }
    }

    init {
        Log.d("[ProfileImageManagerIos] Init ProfileImageManagerIos()")

        profileImageCacheId
            .map { cacheId -> cacheId?.let { baseCache.getData(cacheId) } }
            .onEach { Log.d("[ProfileImageManagerIos] profileImageCacheId - byteArraySize: ${it?.size}") }
            .collectIn(coroutineScopeIo) { bitmap -> setCurrentProfileImage(bitmap) }
    }
}