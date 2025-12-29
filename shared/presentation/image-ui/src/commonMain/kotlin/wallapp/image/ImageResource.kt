package wallapp.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import wallapp.image.Image.ImageResource
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostManager
import wallapp.image.loader.ImageLoader
import wallapp.pixel.view.UIKitFactory


@Composable
expect fun ImageResource(
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
    uiKitFactory: UIKitFactory? = null,
)
