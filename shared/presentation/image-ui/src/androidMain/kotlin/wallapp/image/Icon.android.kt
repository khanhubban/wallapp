package wallapp.image

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import wallapp.resource.Resource

@Composable
actual fun Icon(
    image: Image,
    tint: Color,
    modifier: Modifier,
) {
    require(image is Image.ImageResource)
    when (val resource = image.resource) {
        is Resource.Vector -> {
            Icon(
                imageVector = resource.imageVector,
                contentDescription = image.contentDescription,
                modifier = modifier,
                tint = tint,
            )
        }

        is Resource.Drawable -> {
            Icon(
                painter = painterResource(id = resource.drawableRes),
                contentDescription = image.contentDescription,
                modifier = modifier,
                tint = tint,
            )
        }

        else -> {
            TODO("Add Icon support Image type ${image::class.simpleName}")
        }
    }
}