package wallapp.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.graphics.Color
import wallapp.graphics.composeColor

@Composable
fun ImageColor(
    color: Color,
    modifier: Modifier,
    alignment: Alignment = Alignment.Center,
) {
//    val shape = image.shape

    Box(
        modifier = modifier
            .fillMaxSize()
//            .clipIfNotNull(shape)
//            .border(width = 2.dp, color = Color.Green)
            .background(color = color.composeColor),
    )
}