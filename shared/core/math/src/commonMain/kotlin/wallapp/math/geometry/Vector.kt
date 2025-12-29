package wallapp.math.geometry

import kotlin.math.sqrt

data class Vector(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {

    fun length(): Float {
        return sqrt((x * x
                + y * y
                + z * z)
            .toDouble()).toFloat()
    }

    // http://en.wikipedia.org/wiki/Cross_product
    fun crossProduct(other: Vector): Vector {
        return Vector(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    // http://en.wikipedia.org/wiki/Dot_product
    fun dotProduct(other: Vector): Float {
        return (x * other.x
                + y * other.y
                + z * other.z)
    }

    fun scale(f: Float): Vector {
        return Vector(
            x * f,
            y * f,
            z * f
        )
    }
}