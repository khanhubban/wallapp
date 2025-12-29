package wallapp.pixel.view

import androidx.compose.ui.layout.ContentScale

object ViewContentScaleMapperCompose {

    fun map(viewContentScale: ViewContentScale): ContentScale {
        return when (viewContentScale) {
            is ViewContentScale.Crop -> ContentScale.Crop
            is ViewContentScale.FillHeight -> ContentScale.FillHeight
            is ViewContentScale.FillWidth -> ContentScale.FillWidth
            is ViewContentScale.Fit -> ContentScale.Fit
            is ViewContentScale.FillBounds -> ContentScale.FillBounds
        }
    }
}