package wallapp.image.prefetch

import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import wallapp.image.imageUrl
import wallapp.image.loader.ImageLoaderCoil
import wallapp.pixel.compose.px
import wallapp.util.CancellableWork
import wallapp.image.loader.ImageLoader as ImageLoaderHolder

class ImagePrefetcherCoilAndroid(
    private val context: Context,
    imageLoader: ImageLoaderHolder,
) : ImagePrefetcher {

//    companion object {
//        val Log = Logger("ImagePrefetch")
//    }

    private val imageLoader: ImageLoader by lazy {
        (imageLoader as ImageLoaderCoil).imageLoaderCoil
    }

    override fun prefetch(imagePrefetchData: ImagePrefetchData) {
        imagePrefetchData.entries?.forEach {
            prefetch(it)
        }
    }

    override fun prefetch(
        imagePrefetchEntry: ImagePrefetchEntry,
        onCompletion: (() -> Unit)?,
    ): CancellableWork? {
        val imageViewState = imagePrefetchEntry.imageViewState
        val image = imageViewState.image
        val imageUrl = requireNotNull(image.imageUrl) { "Image URL is null - $image" }
        val size = image.imageSize?.size

//        Log.d("Prefetching image (${width}x${height}): $imageUrl")

        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .let {
                // Optional, but setting a ViewSizeResolver will conserve memory by limiting the
                // size the image should be preloaded into memory at.
                if (size != null) {
                    it.size(size.width.px!!, size.height.px!!)
                } else {
                    it
                }
            }
            .build()
        imageLoader.enqueue(request)
        return null
    }
}