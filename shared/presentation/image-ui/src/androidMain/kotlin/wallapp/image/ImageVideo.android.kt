package wallapp.image

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ImageVideo(
    url: String,
    imageVideoState: ImageVideoState,
    modifier: Modifier = Modifier,
) {
    ImageVideoExoplayer(url = url, imageVideoState = imageVideoState, modifier = modifier)
}

@Composable
fun ImageVideo(
    @RawRes rawRes: Int,
    imageVideoState: ImageVideoState,
    modifier: Modifier = Modifier,
) {
    ImageVideoExoplayer(rawRes = rawRes, imageVideoState = imageVideoState, modifier = modifier)
}
