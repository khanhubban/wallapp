package wallapp.pixel.shape

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ShapeClipperComposable : ShapeClipper {

    @Composable
    fun clip(modifier: Modifier, shapeSpec: ShapeSpec): Modifier

}

@Composable
fun Modifier.clip(shapeSpec: ShapeSpec?, shapeClipper: ShapeClipper): Modifier {
    if (shapeSpec == null) return this
    require(shapeClipper is ShapeClipperComposable)
    return shapeClipper.clip(this, shapeSpec)
}