package wallapp.math.geometry

import wallapp.math.distanceBetweenPoints

data class LatLong(
    val latitude: Double,
    val longitude: Double,
)


val List<LatLong>.smallestLatitude: Double
    get() = minByOrNull { it.latitude }!!.latitude
val List<LatLong>.largestLatitude: Double
    get() = maxByOrNull { it.latitude }!!.latitude
val List<LatLong>.smallestLongitude: Double
    get() = minByOrNull { it.longitude }!!.longitude
val List<LatLong>.largestLongitude: Double
    get() = maxByOrNull { it.longitude }!!.longitude

val List<LatLong>.extremePoints: Pair<LatLong, LatLong>
    get() {
        require(size >= 2)
        return LatLong(smallestLatitude, smallestLongitude) to
                LatLong(largestLatitude, largestLongitude)
    }

val List<LatLong>.startAndEndPoints: Pair<LatLong, LatLong>?
    get() {
        if (size < 2) {
            return null
        }

        return first() to last()
    }

val List<LatLong>.center: LatLong
    get() {
        if (size == 1) {
            return first()
        }

        val pointA = first()
        val pointB = last()

        val latitude = (pointA.latitude + pointB.latitude) * 0.5
        val longitude = (pointA.longitude + pointB.longitude) * 0.5

        return LatLong(latitude, longitude)
    }

fun Pair<LatLong, LatLong>.lerp(time: Double): LatLong {
    return LatLong(
        wallapp.math.lerp(first.latitude, second.latitude, time),
        wallapp.math.lerp(first.longitude, second.longitude, time),
    )
}

val Pair<LatLong, LatLong>.distance: Double
    get() = distanceBetweenPoints(first.latitude, first.longitude, second.latitude, second.longitude)
