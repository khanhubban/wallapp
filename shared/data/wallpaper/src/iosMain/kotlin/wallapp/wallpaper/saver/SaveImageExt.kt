package wallapp.wallpaper.saver

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.Foundation.NSArray
import platform.Foundation.NSError
import platform.Foundation.NSFastEnumerationProtocol
import platform.Foundation.NSPredicate
import platform.Foundation.NSString
import platform.Foundation.arrayWithObject
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHAssetCollection
import platform.Photos.PHAssetCollectionChangeRequest
import platform.Photos.PHAssetCollectionSubtypeAny
import platform.Photos.PHAssetCollectionTypeAlbum
import platform.Photos.PHFetchOptions
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImage
import wallapp.wallpaper.saver.WallpaperSaverManagerIos.Companion.Log

/**
 * Saves the image to the system media library. Use this when using just the "Add to Photos"
 * permission.
 */
@Suppress("RedundantSuspendModifier")
@OptIn(ExperimentalForeignApi::class)
internal suspend fun addImageToLibrary(image: UIImage, saveImageCallback: SaveImageCallback) {
    val sharedPhotoLibrary = PHPhotoLibrary.sharedPhotoLibrary()
    Log.d("saveImageToLibrary() - start")

    // Unique identifier for the saved image as returned by the system.
    var savedLocalIdentifier: String? = null

    sharedPhotoLibrary.performChanges({
        memScoped {
            Log.d("saveImageToLibrary() - commence")
            val creationRequest = PHAssetChangeRequest.creationRequestForAssetFromImage(image)
            val assetPlaceholder = creationRequest.placeholderForCreatedAsset

            savedLocalIdentifier = assetPlaceholder?.localIdentifier
        }
    }) { success: Boolean, error: NSError? ->
        val result = SaveImageResult.from(success, savedLocalIdentifier, error)
        saveImageCallback.invoke(result)
        Log.d("addImageToLibrary() - result: $result")
    }
}


/**
 * Saves the image to the system media library and adds it to the specified album. Use this when
 * using the "Full Photos Access" permission.
 */
@Suppress("RedundantSuspendModifier")
@OptIn(ExperimentalForeignApi::class)
internal suspend fun saveImageToAlbum(
    image: UIImage,
    albumName: String,
    saveImageCallback: SaveImageCallback,
) {
    val sharedPhotoLibrary = PHPhotoLibrary.sharedPhotoLibrary()
    Log.d("saveImageToAlbum($albumName) - start")

    // Unique identifier for the saved image as returned by the system.
    var savedLocalIdentifier: String? = null

    sharedPhotoLibrary.performChanges({
        memScoped {
            Log.d("saveImageToAlbum() - commence")
            val creationRequest = PHAssetChangeRequest.creationRequestForAssetFromImage(image)
            val assetPlaceholder = creationRequest.placeholderForCreatedAsset
            var albumChangeRequest: PHAssetCollectionChangeRequest? = null

            val fetchOptions = PHFetchOptions().apply {
                predicate = NSPredicate.predicateWithFormat("title = %@", albumName as NSString)
            }
            val collection = PHAssetCollection.fetchAssetCollectionsWithType(
                PHAssetCollectionTypeAlbum,
                PHAssetCollectionSubtypeAny, fetchOptions)

            val assetCollection = collection.firstObject as? PHAssetCollection
            albumChangeRequest = assetCollection?.let {
                PHAssetCollectionChangeRequest.changeRequestForAssetCollection(it)
            } ?: PHAssetCollectionChangeRequest.creationRequestForAssetCollectionWithTitle(albumName)

            val nsArray: NSFastEnumerationProtocol = NSArray.arrayWithObject(assetPlaceholder!!) as NSFastEnumerationProtocol
            albumChangeRequest.addAssets(nsArray)

            savedLocalIdentifier = assetPlaceholder.localIdentifier
        }
    }) { success: Boolean, error: NSError? ->
        val result = SaveImageResult.from(success, savedLocalIdentifier, error)
        saveImageCallback.invoke(result)
        Log.d("addImageToLibrary() - result: $result")
    }
}