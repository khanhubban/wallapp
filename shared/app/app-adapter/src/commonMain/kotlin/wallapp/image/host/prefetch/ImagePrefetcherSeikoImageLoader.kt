package wallapp.image.host.prefetch

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.model.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import wallapp.coroutine.CoroutineScopes
import wallapp.image.imageUrl
import wallapp.image.loader.ImageLoaderSeiko
import wallapp.image.prefetch.ImagePrefetchData
import wallapp.image.prefetch.ImagePrefetchEntry
import wallapp.image.prefetch.ImagePrefetcher
import wallapp.image.seiko.cachePolicy
import wallapp.image.seiko.seikoImageLoaderSizeResolverWorkaround
import wallapp.jobs.CancellableWorkDefault
import wallapp.util.CancellableWork


class ImagePrefetcherSeikoImageLoader(
    imageLoaderSeiko: ImageLoaderSeiko,
    private val coroutineScopes: CoroutineScopes,
) : ImagePrefetcher {

//    companion object {
//        val Log = Logger("ImagePrefetch")
//    }

    private val coroutineScopePrefetch: CoroutineScope
        get() = coroutineScopes.prefetch

    private val imageLoader: ImageLoader by lazy { imageLoaderSeiko.imageLoaderSeiko }
    private val jobs: MutableMap<ImagePrefetchEntry, CancellableWork> = mutableMapOf()

    override fun prefetch(imagePrefetchData: ImagePrefetchData) {
        val currentImageViewStates = imagePrefetchData.entries?.toSet() ?: emptySet()
        val imageViewStatesToRemove = jobs.keys - currentImageViewStates
        imageViewStatesToRemove.forEach { entry ->
//            Log.v("Cancelling prefetch job for ${entry.imageViewState.image.imageUrl}")
            jobs[entry]?.cancel()
            jobs.remove(entry)
        }

        imagePrefetchData.entries?.forEach { entry ->
            if (!jobs.containsKey(entry)) {
                jobs[entry] = prefetch(imagePrefetchEntry = entry)
            }
        }
    }

    override fun prefetch(
        imagePrefetchEntry: ImagePrefetchEntry,
        onCompletion: (() -> Unit)?,
    ): CancellableWork {
        val imageViewState = imagePrefetchEntry.imageViewState
        val image = imageViewState.image
        val imageUrl = requireNotNull(image.imageUrl) { "Image URL is null - $image" }

        val imageRequest = ImageRequest {
            data(imageUrl)
            image.imageSize?.size?.seikoImageLoaderSizeResolverWorkaround?.also {
                // Override the default behaviour for AutoSizeBox. It uses the view's smaller of
                // the two dimensions to calculate the maxSize of the decoded image. This is not
                // ideal for images that are not close to square. (#156)
                size(it)
            }
            options {
                memoryCachePolicy = imagePrefetchEntry.imageCacheSpec.memoryCachePolicy.cachePolicy
                diskCachePolicy = imagePrefetchEntry.imageCacheSpec.diskCachePolicy.cachePolicy
            }
        }

        return CancellableWorkDefault(
            imageLoader.async(imageRequest)
//                .onEach { Log.d("Request prefetched image: $imageLog") }
                .launchIn(coroutineScopePrefetch).also {
                    it.invokeOnCompletion { onCompletion?.invoke() }
                }
        )
    }

}