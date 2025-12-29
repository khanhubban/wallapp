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
import wallapp.content.view.UIKitFactoryIos
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
    val arbitratedColorFilter: ColorFilter? = colorFilter
        ?: image.tintColorToken?.let { ColorFilter.tint(ThemeColorTypeMapper.map(it)) }

    ImageResource(
        resource = image.resource,
        contentDescription = image.contentDescription,
        loadingImage = loadingImage,
        imageSize = imageSize,
        imageVideoState = imageVideoState,
        imageHostManager = imageHostManager,
        imageHashDecoder = imageHashDecoder,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = arbitratedColorFilter,
        alignment = alignment,
        uiKitFactory = uiKitFactory as UIKitFactoryIos?,
    )
}

@Composable
private fun ImageResource(
    resource: Resource,
    contentDescription: String?,
    loadingImage: Image?,
    imageSize: ImageSize?,
    imageVideoState: ImageVideoState?,
    imageHostManager: ImageHostManager,
    imageHashDecoder: ImageHashDecoder,
    modifier: Modifier,
    contentScale: ContentScale,
    colorFilter: ColorFilter?,
    alignment: Alignment,
    uiKitFactory: UIKitFactoryIos?,
) {
    when (resource) {
        is Resource.UrlImage -> {
            ImageUrl(
                imageUrl = resource.url,
                contentDescription = contentDescription,
                loadingImage = loadingImage,
                imageSize = imageSize,
                imageHostManager = imageHostManager,
                imageHashDecoder = imageHashDecoder,
                modifier = modifier,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = colorFilter,
            )

//            ImageUrlIos(
//                contentDescription = image.contentDescription,
//                modifier = modifier,
//                contentScale = contentScale,
//            )

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
                contentDescription = contentDescription,
                modifier = modifier,
//                    .clipIfNotNull(image.shape),
                contentScale = contentScale,
                colorFilter = colorFilter,
                alignment = alignment,
            )
        }
        is Resource.Color -> {
            ImageColor(
                color = resource.color,
                modifier = modifier,
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
        is Resource.AnimatedImage -> {
            val fallback: Resource? = resource.fallback
            /**
             * If fallback is not null, then we will use the fallback image resource.
             */
            if (fallback != null) {
                ImageResource(
                    resource = fallback,
                    contentDescription = contentDescription,
                    imageSize = null,
                    imageVideoState = imageVideoState,
                    loadingImage = loadingImage,
                    imageHostManager = imageHostManager,
                    imageHashDecoder = imageHashDecoder,
                    modifier = modifier,
                    contentScale = contentScale,
                    colorFilter = colorFilter,
                    alignment = alignment,
                    uiKitFactory = uiKitFactory,
                )
            }
        }
        is Resource.RawByteArray -> {
            ImageRawByteArray(
                byteArray = resource.byteArray,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = colorFilter,
                alignment = alignment,
            )
        }
        is Resource.LocalImageAssetFile -> {
            Image(
                painter = painterResource(resource.drawableResource),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = colorFilter,
                alignment = alignment,
            )
        }
        is Resource.UrlVideo -> {
            ImageVideo(
                url = resource.url,
                imageVideoState = requireNotNull(imageVideoState) { "Rendering of UrlVideo requires imageVideoSpec" },
                uiKitFactory = requireNotNull(uiKitFactory) { "Rendering of UrlVideo requires uiKitFactory" },
                modifier = modifier,
            )
        }
        is Resource.AnimatedAssetFile -> {
            ImageVideo(
                fileName = resource.fileName,
                imageVideoState = requireNotNull(imageVideoState) { "Rendering of UrlVideo requires imageVideoSpec" },
                uiKitFactory = requireNotNull(uiKitFactory) { "Rendering of UrlVideo requires uiKitFactory" },
                modifier = modifier,
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
