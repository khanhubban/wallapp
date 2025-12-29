package wallapp.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import wallapp.image.Image.ImageResource
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostManager
import wallapp.image.loader.ImageLoader
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.UIKitFactory
import wallapp.resource.Resource

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
            ImageUrl(
                imageUrl = resource.url,
                contentDescription = image.contentDescription,
                loadingImage = loadingImage,
                imageSize = imageSize,
                imageHostManager = imageHostManager,
                imageHashDecoder,
                modifier = modifier,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = arbitratedColorFilter,
            )

//            requireNotNull(imageLoader) { "Rendering of URL requires imageLoader" }
//            requireNotNull(imageMemoryCacheKeyManager) { "Rendering of URL requires imageMemoryCacheKeyManager" }
//            ImageUrl(
//                imageUrl = resource.url,
//                contentDescription = image.contentDescription,
//                shape = null,//image.shape,
//                size = null,//image.size?.coilSize,
//                imageLoader = imageLoader.imageLoaderCoil,
//                imageMemoryCacheKeyManager = imageMemoryCacheKeyManager,
//                modifier = modifier,
//                contentScale = contentScale,
//                colorFilter = colorFilter,
//            )
        }
        is Resource.Vector -> {
            Image(
                imageVector = resource.imageVector,
                contentDescription = image.contentDescription,
                modifier = modifier,
//                    .clipIfNotNull(image.shape),
                contentScale = contentScale,
                colorFilter = arbitratedColorFilter,
                alignment = alignment,
            )
        }
        is Resource.Color -> {
            ImageColor(
                color = resource.color,
                modifier = modifier,
                alignment = alignment,
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
        is Resource.DrawableResourceCompose -> {
            Image(
                painter = painterResource(resource.drawable),
                contentDescription = null, // contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = arbitratedColorFilter,
                alignment = alignment,
            )
        }
        else -> {
//            TODO("Add Image support for Resource type ${resource::class.simpleName}")
            UnhandledResource(resource, modifier)
        }
    }
}

@Composable
private fun UnhandledResource(
    resource: Resource,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(Color.Green),
        contentAlignment = Alignment.Center,
    ) {
        Text("UR")
    }
}
