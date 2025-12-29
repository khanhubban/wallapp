package wallapp.graphics

import wallapp.unit.Size
import coil.size.Size as CoilSize

val Size.coilSize: CoilSize
    get() = CoilSize(
        width = width.coilDimension,
        height = height.coilDimension,
    )