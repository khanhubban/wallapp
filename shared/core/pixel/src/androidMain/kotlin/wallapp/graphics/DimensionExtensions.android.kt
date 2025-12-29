package wallapp.graphics

import wallapp.unit.Dimension
import coil.size.Dimension as CoilDimension

val Dimension.coilDimension: CoilDimension
    get() = when (this) {
        is Dimension.Pixels -> CoilDimension.Pixels(this.px)
        else -> { error("Unsupported Dimension: $this") }
    }