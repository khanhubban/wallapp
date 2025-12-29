package wallapp.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.seiko.imageloader.LocalImageLoader
import wallapp.image.loader.ImageLoader
import wallapp.image.loader.ImageLoaderSeiko

@Composable
fun WallAppCompositionLocalProvider(
    imageLoader: ImageLoader,
    content: @Composable () -> Unit,
) {
    val imageLoaderSeiko = if (imageLoader is ImageLoaderSeiko) {
        imageLoader.imageLoaderSeiko
    } else {
        null
    }

    if (imageLoaderSeiko != null) {
        CompositionLocalProvider(
            LocalImageLoader provides imageLoaderSeiko,
        ) {
            content()
        }
    } else {
        content()
    }
}