package wallapp.graphics

import androidx.compose.runtime.Immutable
import wallapp.graphics.Color.Companion.Transparent
import wallapp.graphics.Color.Companion.Unspecified
import kotlin.jvm.JvmInline


@Immutable
@JvmInline
value class Color(val value: ULong) {

//    val colorSpace: ColorSpace
//        get() = ColorSpaces.getColorSpace((value and 0x3fUL).toInt())

    /**
     * Converts this color from its color space to the specified color space.
     * The conversion is done using the default rendering intent as specified
     * by [ColorSpace.connect].
     *
     * @param colorSpace The destination color space, cannot be null
     *
     * @return A non-null color instance in the specified color space
     */
//    fun convert(colorSpace: ColorSpace): Color {
//        val thisColorSpace = this.colorSpace
//        if (colorSpace == thisColorSpace) {
//            return this // nothing to convert
//        }
//        val connector = thisColorSpace.connect(colorSpace)
//        return connector.transformToColor(red, green, blue, alpha)
//    }

    val red: Float
        get() {
            return if ((value and 0x3fUL) == 0UL) {
                ((value shr 48) and 0xffUL).toFloat() / 255.0f
            } else {
                Float16(((value shr 48) and 0xffffUL).toShort())
                    .toFloat()
            }
        }

    /**
     * Returns the value of the green component in the range defined by this
     * color's color space (see [ColorSpace.getMinValue] and
     * [ColorSpace.getMaxValue]).
     *
     * If this color's color model is not [RGB][ColorModel.Rgb],
     * calling this is the second component of the ColorSpace.
     *
     * @see alpha
     * @see red
     * @see blue
     */
    val green: Float
        get() {
            return if ((value and 0x3fUL) == 0UL) {
                ((value shr 40) and 0xffUL).toFloat() / 255.0f
            } else {
                Float16(((value shr 32) and 0xffffUL).toShort())
                    .toFloat()
            }
        }

    /**
     * Returns the value of the blue component in the range defined by this
     * color's color space (see [ColorSpace.getMinValue] and
     * [ColorSpace.getMaxValue]).
     *
     * If this color's color model is not [RGB][ColorModel.Rgb],
     * calling this is the third component of the ColorSpace.
     *
     * @see alpha
     * @see red
     * @see green
     */
    val blue: Float
        get() {
            return if ((value and 0x3fUL) == 0UL) {
                ((value shr 32) and 0xffUL).toFloat() / 255.0f
            } else {
                Float16(((value shr 16) and 0xffffUL).toShort())
                    .toFloat()
            }
        }

    /**
     * Returns the value of the alpha component in the range `[0..1]`.
     *
     * @see red
     * @see green
     * @see blue
     */
    val alpha: Float
        get() {
            return if ((value and 0x3fUL) == 0UL) {
                ((value shr 56) and 0xffUL).toFloat() / 255.0f
            } else {
                ((value shr 6) and 0x3ffUL).toFloat() / 1023.0f
            }
        }

    operator fun component1(): Float = red

    operator fun component2(): Float = green

    operator fun component3(): Float = blue

    operator fun component4(): Float = alpha

//    operator fun component5(): ColorSpace = colorSpace

    /**
     * Copies the existing color, changing only the provided values. The [ColorSpace][colorSpace]
     * of the returned [Color] is the same as this [colorSpace].
     */
    fun copy(
        alpha: Float = this.alpha,
        red: Float = this.red,
        green: Float = this.green,
        blue: Float = this.blue
    ): Color = Color(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha,
//        colorSpace = this.colorSpace
    )

    /**
     * Returns a string representation of the object. This method returns
     * a string equal to the value of:
     *
     *     "Color($r, $g, $b, $a, ${colorSpace.name})"
     *
     * For instance, the string representation of opaque black in the sRGB
     * color space is equal to the following value:
     *
     *     Color(0.0, 0.0, 0.0, 1.0, sRGB IEC61966-2.1)
     *
     * @return A non-null string representation of the object
     */
    override fun toString(): String {
        return "Color($red, $green, $blue, $alpha)" //, ${colorSpace.name})"
    }

    companion object {
        val Black = Color(0xFF000000)
        val DarkGray = Color(0xFF444444)
        val Gray = Color(0xFF888888)
        val LightGray = Color(0xFFCCCCCC)
        val White = Color(0xFFFFFFFF)
        val Red = Color(0xFFFF0000)
        val Green = Color(0xFF00FF00)
        val Blue = Color(0xFF0000FF)
        val Orange = Color(0xFFff8a00)
        val Yellow = Color(0xFFFFFF00)
        val Cyan = Color(0xFF00FFFF)
        val Purple = Color(0xff9b198f)
        val Magenta = Color(0xFFFF00FF)
        val Transparent = Color(0x00000000)

        /**
         * Because Color is an inline class, this represents an unset value
         * without having to box the Color. It will be treated as [Transparent]
         * when drawn. A Color can compare with [Unspecified] for equality or use
         * [isUnspecified] to check for the unset value or [isSpecified] for any color that isn't
         * [Unspecified].
         */
        val Unspecified = Color(0f, 0f, 0f, 0f, /*ColorSpaces.Unspecified*/)
    }
}

fun Color(
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float = 1f,
//    colorSpace: ColorSpace = ColorSpaces.Srgb
): Color {
//    require(
//        red in colorSpace.getMinValue(0)..colorSpace.getMaxValue(0) &&
//                green in colorSpace.getMinValue(1)..colorSpace.getMaxValue(1) &&
//                blue in colorSpace.getMinValue(2)..colorSpace.getMaxValue(2) &&
//                alpha in 0f..1f
//    ) {
//        "red = $red, green = $green, blue = $blue, alpha = $alpha outside the range for $colorSpace"
//    }

//    if (colorSpace.isSrgb) {
        val argb = (
                ((alpha * 255.0f + 0.5f).toInt() shl 24) or
                        ((red * 255.0f + 0.5f).toInt() shl 16) or
                        ((green * 255.0f + 0.5f).toInt() shl 8) or
                        (blue * 255.0f + 0.5f).toInt()
                )
        return Color(value = (argb.toULong() and 0xffffffffUL) shl 32)
//    }

//    require(colorSpace.componentCount == 3) {
//        "Color only works with ColorSpaces with 3 components"
//    }
//
//    val id = colorSpace.id
//    require(id != ColorSpace.MinId) {
//        "Unknown color space, please use a color space in ColorSpaces"
//    }
//
//    val r = Float16(red)
//    val g = Float16(green)
//    val b = Float16(blue)
//
//    val a = (kotlin.math.max(0.0f, kotlin.math.min(alpha, 1.0f)) * 1023.0f + 0.5f).toInt()
//
//    // Suppress sign extension
//    return Color(
//        value = (
//                ((r.halfValue.toULong() and 0xffffUL) shl 48) or (
//                        (g.halfValue.toULong() and 0xffffUL) shl 32
//                        ) or (
//                        (b.halfValue.toULong() and 0xffffUL) shl 16
//                        ) or (
//                        (a.toULong() and 0x3ffUL) shl 6
//                        ) or (
//                        id.toULong() and 0x3fUL
//                        )
//                )
//    )
}

/**
 * Creates a new [Color] instance from an ARGB color int.
 * The resulting color is in the [sRGB][ColorSpaces.Srgb]
 * color space.
 *
 * @param color The ARGB color int to create a <code>Color</code> from.
 * @return A non-null instance of {@link Color}
 */
fun Color(/*@ColorInt*/ color: Int): Color {
    return Color(value = color.toULong() shl 32)
}

/**
 * Creates a new [Color] instance from an ARGB color int.
 * The resulting color is in the [sRGB][ColorSpaces.Srgb]
 * color space. This is useful for specifying colors with alpha
 * greater than 0x80 in numeric form without using [Long.toInt]:
 *
 *     val color = Color(0xFF000080)
 *
 * @param color The 32-bit ARGB color int to create a <code>Color</code>
 * from
 * @return A non-null instance of {@link Color}
 */
fun Color(color: Long): Color {
    return Color(value = (color.toULong() and 0xffffffffUL) shl 32)
}

/**
 * Creates a new [Color] instance from an ARGB color components.
 * The resulting color is in the [sRGB][ColorSpaces.Srgb]
 * color space. The default alpha value is `0xFF` (opaque).
 *
 * @param red The red component of the color, between 0 and 255.
 * @param green The green component of the color, between 0 and 255.
 * @param blue The blue component of the color, between 0 and 255.
 * @param alpha The alpha component of the color, between 0 and 255.
 *
 * @return A non-null instance of {@link Color}
 */
fun Color(
    /*@IntRange(from = 0, to = 0xFF)*/
    red: Int,
    /*@IntRange(from = 0, to = 0xFF)*/
    green: Int,
    /*@IntRange(from = 0, to = 0xFF)*/
    blue: Int,
    /*@IntRange(from = 0, to = 0xFF)*/
    alpha: Int = 0xFF
): Color {
    val color = ((alpha and 0xFF) shl 24) or
            ((red and 0xFF) shl 16) or
            ((green and 0xFF) shl 8) or
            (blue and 0xFF)
    return Color(color)
}

fun Color.red(): Double = red.toDouble()
fun Color.green(): Double = green.toDouble()
fun Color.blue(): Double = blue.toDouble()
fun Color.alpha(): Double = alpha.toDouble()