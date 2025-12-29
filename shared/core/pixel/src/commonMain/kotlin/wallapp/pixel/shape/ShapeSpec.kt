package wallapp.pixel.shape

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop

@Immutable
@SealedInterop.Enabled
sealed class ShapeSpec {

    @Immutable
    data class Single(
        val shapeStyle: ShapeStyle?,
        val shapeSize: ShapeSize?,
    ) : ShapeSpec()

    @Immutable
    data class Multiple(
        val shapeSpecs: List<Single>,
    ) : ShapeSpec()

    companion object {
        val Preset by lazy {
            ShapeSpec(
                shapeStyle = ShapeStyle.Rectangle,
                shapeSize = null,
            )
        }
    }
}

fun ShapeSpec(
    shapeStyle: ShapeStyle?,
    shapeSize: ShapeSize?,
): ShapeSpec.Single = ShapeSpec.Single(
    shapeStyle = shapeStyle,
    shapeSize = shapeSize,
)

fun ShapeSpec(
    vararg shapeSpecs: ShapeSpec.Single,
): ShapeSpec.Multiple = ShapeSpec.Multiple(
    shapeSpecs = shapeSpecs.toList(),
)