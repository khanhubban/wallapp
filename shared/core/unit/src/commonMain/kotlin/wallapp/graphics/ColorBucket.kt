package wallapp.graphics

enum class ColorBucket {
    White,
    Black,
    Blue,
    Green,
    Yellow,
    Orange,
    Red,
    Purple,
}

val Color.colorBucket: ColorBucket
    get() = ColorBucket(this)

fun ColorBucket(color: Int): ColorBucket = ColorBucket(Color(color))

fun ColorBucket(color: Color): ColorBucket {
    val red = (color.red * 255).toInt()
    val green = (color.green * 255).toInt()
    val blue = (color.blue * 255).toInt()
    return ColorBucket(red, green, blue)
}

@OptIn(ExperimentalStdlibApi::class)
fun ColorBucket(red: Int, green: Int, blue: Int): ColorBucket {
    return when {
        red > 200 && green > 200 && blue > 200 -> ColorBucket.White
        red < 50 && green < 50 && blue < 50 -> ColorBucket.Black
        red < 100 && green < 100 && blue > 150 -> ColorBucket.Blue
        red < 100 && green > 150 && blue < 100 -> ColorBucket.Green
        red > 150 && green > 80 && green < 170 && blue < 80 -> ColorBucket.Orange
        red > 150 && green > 150 && blue < 100 -> ColorBucket.Yellow
        red > 150 && green < 80 && blue < 80 -> ColorBucket.Red
        red > 100 && green < 100 && blue > 100 -> ColorBucket.Purple
//        else -> throw IllegalArgumentException("Color does not match any known bucket: r: $red, g: $green, b: $blue, 0x${Color(red, green, blue).toArgb().toHexString()}")
        else -> getColorFamilyFallback(red, green, blue)
    }
}

fun getColorFamilyFallback(red: Int, green: Int, blue: Int): ColorBucket {
    return when {
        red >= green && red >= blue -> {
            when {
                green >= 80 && blue >= 80 -> ColorBucket.White
                green < 80 && blue < 80 -> ColorBucket.Red
                else -> ColorBucket.Orange
            }
        }
        green >= red && green >= blue -> {
            when {
                red >= 100 && blue >= 100 -> ColorBucket.White
                red < 100 && blue < 100 -> ColorBucket.Green
                else -> ColorBucket.Yellow
            }
        }
        blue >= red && blue >= green -> {
            when {
                red >= 100 && green >= 100 -> ColorBucket.White
                red < 100 && green < 100 -> ColorBucket.Blue
                else -> ColorBucket.Purple
            }
        }
        else -> ColorBucket.Black  // Default catch-all, though it shouldn't be reached
    }
}
