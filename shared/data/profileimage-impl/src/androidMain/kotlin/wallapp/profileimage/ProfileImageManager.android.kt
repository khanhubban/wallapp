package wallapp.profileimage

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import wallapp.account.AccountManager
import wallapp.bitmap.BitmapMapper.toBitmap
import wallapp.bitmap.BitmapTransformer.clipToSquare
import wallapp.bitmap.BitmapTransformer.resize
import wallapp.coroutine.collectIn
import wallapp.image.Image
import wallapp.image.cache.BitmapDiskCache
import wallapp.log.Log
import wallapp.resources.image.ImageRepository
import wallapp.system.photo.picker.SystemPhotoPicker

class ProfileImageManagerAndroid(
    accountManager: AccountManager,
    systemPhotoPicker: SystemPhotoPicker,
    imageRepository: ImageRepository,
    private val bitmapDiskCache: BitmapDiskCache,
    coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : ProfileImageManagerDefault(accountManager, systemPhotoPicker, imageRepository, coroutineScopeMain) {

    private fun setCurrentProfileImage(bitmap: Bitmap?) {
        Log.d("setCurrentProfileImage()")
        customProfileImage.value = bitmap?.let { Image.from(bitmap) }
    }

    override fun saveProfileImage(bitmapData: ByteArray) {
        coroutineScopeIo.launch {
            val profileImageCacheId = profileImageCacheId.first() ?: return@launch
            Log.d("saveProfileImage() - profileImageCacheId: $profileImageCacheId")
            val systemBitmap = bitmapData.toBitmap()
                .clipToSquare()
                .resize(maxWidth = MaxSize, maxHeight = MaxSize)

            setCurrentProfileImage(systemBitmap)
            bitmapDiskCache.putBitmap(profileImageCacheId, systemBitmap)
        }
    }

    init {
        Log.d("Init ProfileImageManagerAndroid()")

        profileImageCacheId
            .map { cacheId -> cacheId?.let { bitmapDiskCache.getBitmap(cacheId) } }
            .collectIn(coroutineScopeIo) { bitmap -> setCurrentProfileImage(bitmap) }
    }
}
