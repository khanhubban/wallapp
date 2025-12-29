package wallapp.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Icon(
    image: Image,
    tint: Color /*= LocalContentColor.current*/,
) {
    Icon(
        image = image,
        tint = tint,
        modifier = Modifier,
    )
}

@Composable
expect fun Icon(
    image: Image,
    tint: Color /*= LocalContentColor.current*/,
    modifier: Modifier,
)