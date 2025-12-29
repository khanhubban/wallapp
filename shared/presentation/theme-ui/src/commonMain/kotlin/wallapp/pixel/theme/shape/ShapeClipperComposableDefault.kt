package wallapp.pixel.theme.shape

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import wallapp.pixel.shape.ShapeClipperComposable
import wallapp.pixel.shape.ShapeMapperComposable
import wallapp.pixel.shape.ShapeSpec

class ShapeClipperComposableDefault(
    private val shapeMapper: ShapeMapperComposable,
) : ShapeClipperComposable {

    @Composable
    override fun clip(modifier: Modifier, shapeSpec: ShapeSpec): Modifier {
        return when (shapeSpec) {
            is ShapeSpec.Single -> clip(modifier, shapeSpec)
            is ShapeSpec.Multiple -> clip(modifier, shapeSpec)
        }
    }

    @Composable
    fun clip(modifier: Modifier, shapeSpec: ShapeSpec.Single): Modifier {
        val shape = shapeMapper.map(shapeSpec)

        if (shape != null) {
            return modifier.clip(shape)
        }

        return modifier
    }

    @Composable
    fun clip(modifier: Modifier, shapeSpec: ShapeSpec.Multiple): Modifier {
        val shapeSpecs = shapeSpec.shapeSpecs
        var result = modifier
        for (shape in shapeSpecs) {
            result = clip(result, shape)
        }
        return result
    }
}