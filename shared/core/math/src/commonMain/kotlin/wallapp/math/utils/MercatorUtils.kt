/*
 * Copyright 2018 Sarweshkumar C R
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wallapp.math.utils

import wallapp.math.geometry.LatLong
import wallapp.math.toDegrees
import wallapp.math.toRadians
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("NAME_SHADOWING")
object MercatorUtils {
    /**
     * The earth's radius, in meters.
     */
    private const val RADIUS = 6371009.0

    /**
     * Returns the LatLng resulting from moving a distance from a given LatLng
     * in the specified heading
     *
     * @param from     The LatLng from which to start.
     * @param distance The distance to travel.
     * @param heading  The heading in degrees.
     */
    fun latLngOffset(from: LatLong, distance: Double, heading: Double): LatLong {
        // https://www.movable-type.co.uk/scripts/latlong.html
        // φ2 = asin( sin φ1 ⋅ cos δ + cos φ1 ⋅ sin δ ⋅ cos θ )
        // λ2 = λ1 + atan2( sin θ ⋅ sin δ ⋅ cos φ1, cos δ − sin φ1 ⋅ sin φ2 ) */
        var distance = distance
        var heading = heading
        distance /= RADIUS
        heading = heading.toRadians()
        val fromLatitude = from.latitude.toRadians()
        val fromLongitude = from.longitude.toRadians()
        val newLng = asin(sin(fromLatitude) * cos(distance) +
                cos(fromLatitude) * sin(distance) * cos(heading))
        val newLat = fromLongitude + atan2(sin(heading) * sin(distance) * cos(
            fromLatitude),
            cos(distance) - sin(fromLatitude) * sin(newLng))
        return LatLong(newLng.toDegrees(), newLat.toDegrees())
    }

    /**
     * Returns the distance between two LatLng points.
     *
     * @param from Starting point.
     * @param to   Ending point.
     */
    fun distanceBetween(from: LatLong, to: LatLong): Double {
        // https://www.movable-type.co.uk/scripts/latlong.html
        // a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
        // c = 2 ⋅ atan2( √a, √(1−a) )
        // d = R ⋅ c
        val fromLatitude = from.latitude.toRadians()
        val fromLongitude = from.longitude.toRadians()
        val toLatitude = to.latitude.toRadians()
        val toLongitude = to.longitude.toRadians()
        val dLat = toLatitude - fromLatitude
        val dLon = toLongitude - fromLongitude
        val a =
            sin(dLat * 0.5).pow(2.0) + cos(fromLatitude) * cos(toLatitude) *
                    sin(dLon * 0.5).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return RADIUS * c
    }
}