package wallapp.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.memory.MemoryCache
import coil.request.ImageRequest
import wallapp.graphics.coilSize
import wallapp.image.cache.ImageCacheKeyItem
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.loader.ImageLoader
import wallapp.image.loader.imageLoaderCoil
import wallapp.log.Log
import coil.ImageLoader as ImageLoaderCoil


//@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImageUrlCoil(
    modifier: Modifier,
    imageUrl: String,
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    imageSize: ImageSize?,
    imageLoader: ImageLoader,
    imageMemoryCacheKeyManager: ImageCacheKeyManager,
    colorFilter: ColorFilter? = null,
) {
    val imageLoaderCoil = imageLoader.imageLoaderCoil
//    imageLoader.diskCache?.remove(imageUrl)
//    imageLoader.memoryCache?.remove(MemoryCache.Key(imageUrl))

    val placeholderMemoryCacheKey = imageMemoryCacheKeyManager
        .getBestCached(imageUrl, imageSize?.width, imageSize?.height)
        ?.key
        ?.let { it as MemoryCache.Key }

    val onSuccess: ((AsyncImagePainter.State.Success) -> Unit) = {
        val key = it.result.memoryCacheKey
        if (key != null) {
            val cached: MemoryCache.Value? = imageLoaderCoil.memoryCache?.get(key)
            val bitmap = cached?.bitmap
            if (bitmap != null) {
                val keyItem = ImageCacheKeyItem(
                    model = imageUrl,
                    key = key,
                    width = bitmap.width,
                    height = bitmap.height,
                )
                imageMemoryCacheKeyManager.add(keyItem)
            }
        }
    }

    ImageUrlCoil(
        modifier,
        imageUrl,
        contentDescription,
        contentScale,
        alignment,
        imageSize,
        imageLoaderCoil,
        placeholderMemoryCacheKey,
        onSuccess,
        colorFilter,
    )
}

@Composable
private fun ImageUrlCoil(
    modifier: Modifier,
    imageUrl: String,
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    imageSize: ImageSize?,
    imageLoader: ImageLoaderCoil,
    placeholderMemoryCacheKey: MemoryCache.Key?,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    colorFilter: ColorFilter? = null,
) {
    Log.d("ImageUrl [coil]: $imageUrl")
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .placeholderMemoryCacheKey(placeholderMemoryCacheKey)
            .apply {
                if (imageSize != null) {
                    size(imageSize.size.coilSize)
                }
            }
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        imageLoader = imageLoader,
//        error = painterResource(Drawables.PodcastPlaceholder),
        onError = {
            Log.d("Image render error: $it")
        },
        onSuccess = {
            onSuccess?.invoke(it)
        },
        modifier = modifier,
        colorFilter = colorFilter,
        alignment = alignment,
    )
}