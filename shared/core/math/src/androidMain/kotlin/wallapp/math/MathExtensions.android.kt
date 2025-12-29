package wallapp.math

import wallapp.math.geometry.LatLong
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

typealias Matrix = android.opengl.Matrix


const val AVERAGE_RADIUS_OF_EARTH_KM = 6371.0
fun calculateDistanceInKilometers(
    startLat: Double,
    startLng: Double,
    endLat: Double,
    endLng: Double,
): Int {
    val latDistance = Math.toRadians(startLat - endLat)
    val lngDistance = Math.toRadians(startLng - endLng)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(startLat)) * cos(Math.toRadians(endLat))
            * sin(lngDistance / 2) * sin(lngDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return (AVERAGE_RADIUS_OF_EARTH_KM * c).roundToInt()
}

fun calculateDistanceInKilometers(start: LatLong, end: LatLong): Int =
    calculateDistanceInKilometers(start.latitude, start.longitude, end.latitude, end.longitude)

