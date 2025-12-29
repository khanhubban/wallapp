package wallapp.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import wallapp.image.Image.ImageResource
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostManager
import wallapp.image.loader.ImageLoader
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.UIKitFactory
import wallapp.resource.Resource
import wallapp.resource.url
import wallapp.string.quote

@Composable
actual fun ImageResource(
    image: ImageResource,
    imageSize: ImageSize?,
    imageVideoState: ImageVideoState?,
    loadingImage: Image?,
    imageLoader: ImageLoader?,
    imageMemoryCacheKeyManager: ImageCacheKeyManager?,
    imageHostManager: ImageHostManager,
    imageHashDecoder: ImageHashDecoder,
    modifier: Modifier,
    contentScale: ContentScale,
    colorFilter: ColorFilter?,
    alignment: Alignment,
    uiKitFactory: UIKitFactory?,
) {
    val arbitratedColorFilter = colorFilter
        ?: image.tintColorToken?.let { ColorFilter.tint(ThemeColorTypeMapper.map(it)) }

    when (val resource = image.resource) {
        is Resource.UrlImage -> {
            requireNotNull(imageLoader) { "Rendering of URL requires imageLoader (${image.resource.url.quote()})" }
            requireNotNull(imageMemoryCacheKeyManager) { "Rendering of URL requires imageMemoryCacheKeyManager" }
            ImageUrl(
                imageUrl = resource.url,
                contentDescription = image.contentDescription,
                loadingImage = loadingImage,
                shape = null,//image.shape,
                imageSize = imageSize,
                imageLoader = imageLoader,
                imageMemoryCacheKeyManager = imageMemoryCacheKeyManager,
                imageHostManager = imageHostManager,
                imageHashDecoder = imageHashDecoder,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = arbitratedColorFilter,
                alignment = alignment,
            )
        }
        is Resource.Vector -> {
            Image(
                imageVector = resource.imageVector,
                contentDescription = image.contentDescription,
                modifier = modifier,
//                    .clipIfNotNull(image.shape),
                contentScale = contentScale,
                colorFilter = arbitratedColorFilter,
            )
        }
        is Resource.Drawable -> {
            Image(
                painter = painterResource(id = resource.drawableRes),
                contentDescription = image.contentDescription,
                modifier = modifier,
//                    .clipIfNotNull(image.shape),
                contentScale = contentScale,
                colorFilter = arbitratedColorFilter,
            )
        }
        is Resource.Color -> {
            ImageColor(
                color = resource.color,
                modifier = modifier,
            )
        }
        is Resource.AnimatedImage -> {
            ImageLottie(
                animatedImageSpec = resource.spec,
                modifier = modifier,
                contentScale = contentScale,
            )
        }
        is Resource.HashedImage -> {
            ImageHash(
                imageHashDecoder,
                imageHash = resource.imageHash,
                contentDescription = null,
                modifier = modifier,
            )
        }
        is Resource.SystemBitmap -> {
            Image(
                painter = BitmapPainter(resource.bitmap.asImageBitmap()),
                contentDescription = image.contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = arbitratedColorFilter,
                alignment = alignment,
            )
        }
        is Resource.DrawableCompose -> {
            Image(
                painter = org.jetbrains.compose.resources.painterResource(resource.drawable),
                contentDescription = image.contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = arbitratedColorFilter,
                alignment = alignment,
            )
        }
        is Resource.UrlVideo -> {
            ImageVideo(
                url = resource.url,
                imageVideoState = requireNotNull(imageVideoState) { "Rendering of UrlVideo requires imageVideoSpec" },
                modifier = modifier,
            )
        }

        is Resource.Raw -> {
            // Note: likely it's a false assumption that all raw resources are videos, but this is
            // accurate currently. Other assets should be handled by any of the above methods.
            ImageVideo(
                rawRes = resource.rawRes,
                imageVideoState = requireNotNull(imageVideoState) { "Rendering of Raw video requires imageVideoSpec" },
                modifier = modifier,
            )
        }

        else -> {
            TODO("Add Image support for Resource type ${resource::class.simpleName}")
        }
    }
}
