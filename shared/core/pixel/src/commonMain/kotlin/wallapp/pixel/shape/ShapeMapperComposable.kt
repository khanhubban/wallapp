package wallapp.pixel.shape

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

interface ShapeMapperComposable : ShapeMapper {

    @Composable
    fun map(shapeSize: ShapeSize): Shape

    @Composable
    fun map(shapeSpec: ShapeSpec): Shape?
}
