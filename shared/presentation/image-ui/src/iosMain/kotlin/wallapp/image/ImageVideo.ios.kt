package wallapp.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import wallapp.content.view.UIKitFactoryIos

@OptIn(ExperimentalForeignApi::class)
@Composable
fun ImageVideo(
    url: String? = null,
    fileName: String? = null,
    imageVideoState: ImageVideoState,
    uiKitFactory: UIKitFactoryIos,
    modifier: Modifier = Modifier,
) {

    UIKitView(
        factory = {
            uiKitFactory.createVideoPlayer(
                url = url,
                fileName = fileName,
                imageVideoState = imageVideoState,
            )
        },
        update = {
            uiKitFactory.updateVideoPlayer(
                url = url,
                fileName = fileName,
                imageVideoState = imageVideoState,
            )
        },
        onRelease = {
            uiKitFactory.releaseVideoPlayer(
                url = url,
                fileName = fileName,
                imageVideoState = imageVideoState,
            )
        },
        modifier = modifier,
        interactive = false
    )
}