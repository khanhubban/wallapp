package wallapp.pixel.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import kotlin.jvm.JvmName


val Offset.isZero
    @JvmName("isZeroOffset") get() = (x == 0f || x == -0f) && (y == 0f || y == -0f)
val Velocity.isZero
    @JvmName("isZeroVelocity") get() = (x == 0f || x == -0f) && (y == 0f || y == -0f)
