package wallapp.pixel.shape

import co.touchlab.skie.configuration.annotations.EnumInterop
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName


@OptIn(ExperimentalObjCName::class)
@EnumInterop.Enabled
@ObjCName("ClipShapeStyle")
enum class ShapeStyle {
    Circle,
    CutCorner,
    CutCorners,
    Rectangle,
    RoundedCornerBottomStart,
    RoundedCorners,
    RoundedCornersBottom,
    RoundedCornersTop,
    RoundedCornersTopEndBottomStart,
}