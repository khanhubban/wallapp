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
@file:Suppress("unused")

package wallapp.math.utils

import wallapp.math.geometry.LatLong
import wallapp.math.geometry.LatLongControlPoints
import wallapp.math.geometry.PixelControlPoints
import wallapp.math.geometry.Point
import wallapp.math.toRadians
import wallapp.math.utils.MathUtils.angleBetween
import wallapp.math.utils.MathUtils.distanceBetween
import wallapp.math.utils.MathUtils.pixelOffset
import wallapp.math.utils.MercatorUtils.distanceBetween
import wallapp.math.utils.MercatorUtils.latLngOffset

object BezierCurveUtils {

    /**
     * Returns a curve point which lies the given fraction of the way between the
     * origin Point and the destination Point
     *
     * @param from    The Point from which to start.
     * @param iPoint1 Intermediate control point 1.
     * @param iPoint2 Intermediate control point 2.
     * @param to      The Point toward which to travel
     * @param step    Resolution.
     */
    fun computeCurvePoints(
        from: Point,
        iPoint1: Point,
        iPoint2: Point,
        to: Point,
        step: Double,
    ): Point {
        // https://en.wikipedia.org/wiki/B%C3%A9zier_curve
        val arcX =
            (1 - step) * (1 - step) * (1 - step) * from.x + 3 * (1 - step) * (1 - step) * step * iPoint1.x + 3 * (1 - step) * step * step * iPoint2.x + step * step * step * to.x
        val arcY =
            (1 - step) * (1 - step) * (1 - step) * from.y + 3 * (1 - step) * (1 - step) * step * iPoint1.y + 3 * (1 - step) * step * step * iPoint2.y + step * step * step * to.y
        return Point(arcX.toInt(), arcY.toInt())
    }

    /**
     * Returns the LatLng which lies the given fraction of the way between the
     * origin LatLng and the destination LatLng
     *
     * @param from     The LatLng from which to start.
     * @param iLatLng1 Intermediate control point 1.
     * @param iLatLng2 Intermediate control point 2.
     * @param to       The LatLng toward which to travel
     * @param step     Resolution.
     */
    fun computeCurvePoints(
        from: LatLong,
        iLatLng1: LatLong,
        iLatLng2: LatLong,
        to: LatLong,
        step: Double,
    ): LatLong {
        // https://en.wikipedia.org/wiki/B%C3%A9zier_curve
        val arcLatitude =
            (1 - step) * (1 - step) * (1 - step) * from.latitude + 3 * (1 - step) * (1 - step) * step * iLatLng1.latitude + 3 * (1 - step) * step * step * iLatLng2.latitude + step * step * step * to.latitude
        val arcLongitude =
            (1 - step) * (1 - step) * (1 - step) * from.longitude + 3 * (1 - step) * (1 - step) * step * iLatLng1.longitude + 3 * (1 - step) * step * step * iLatLng2.longitude + step * step * step * to.longitude
        return LatLong(arcLatitude, arcLongitude)
    }

    /**
     * Returns the LatLngControlPoints which determines the shape of the curve
     *
     * @param alpha Curve coefficient
     * @param from  The LatLng from which to start.
     * @param to    The LatLng toward which to travel
     */
    fun computeCurveControlPoints(
        alpha: Double,
        from: LatLong,
        to: LatLong,
    ): LatLongControlPoints {
        val curveTangentAngle = 90 * alpha
        val distanceBetween = distanceBetween(from, to)
        val lineHeadingFromStart = angleBetween(from, to)
        val lineHeadingFromEnd = angleBetween(to, from)
        var controlPointHeading1 = 0.0
        var controlPointHeading2 = 0.0
        if (lineHeadingFromStart == 0.0 && lineHeadingFromEnd == 180.0) {
            controlPointHeading1 = lineHeadingFromStart + curveTangentAngle
            controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle
        } else if (lineHeadingFromStart == 180.0 && lineHeadingFromEnd == 0.0) {
            controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle
            controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle
        } else if (lineHeadingFromStart > 0 && lineHeadingFromEnd > 180) {
            controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle
            controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle
            if (controlPointHeading2 >= 360) {
                controlPointHeading2 -= 360
            }
        } else if (lineHeadingFromStart > 180 && lineHeadingFromEnd > 0) {
            controlPointHeading1 = lineHeadingFromStart + curveTangentAngle
            controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle
            if (controlPointHeading1 >= 360) {
                controlPointHeading1 -= 360
            }
        }
        val pA = latLngOffset(from, distanceBetween / 3, controlPointHeading1)
        val pB = latLngOffset(to, distanceBetween / 3, controlPointHeading2)
        return LatLongControlPoints(pA, pB)
    }

    /**
     * Returns the PixelControlPoints which determines the shape of the curve
     *
     * @param alpha Curve coefficient
     * @param from  The Point (screen pixel) from which to start.
     * @param to    The Point (screen pixel) toward which to travel
     */
    fun computeCurveControlPoints(
        alpha: Double,
        from: Point,
        to: Point,
    ): PixelControlPoints {
        val curveTangentAngle = 90 * alpha
        val distanceBetween = distanceBetween(from, to)
        val lineHeadingFromStart = angleBetween(from, to)
        val lineHeadingFromEnd = angleBetween(to, from)
        var controlPointHeading1 = 0.0
        var controlPointHeading2 = 0.0
        if (lineHeadingFromStart < 90 && lineHeadingFromEnd < 270) {
            controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle
            controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle
        } else if (lineHeadingFromStart < 270 && lineHeadingFromEnd < 90) {
            controlPointHeading1 = lineHeadingFromStart + curveTangentAngle
            controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle
        } else if (lineHeadingFromStart >= 90 && lineHeadingFromEnd >= 270) {
            controlPointHeading1 = lineHeadingFromStart + curveTangentAngle
            controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle
        } else if (lineHeadingFromStart >= 270 && lineHeadingFromEnd >= 90) {
            controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle
            controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle
        }
        val pA = pixelOffset(from, distanceBetween / 3, controlPointHeading1.toRadians())
        val pB = pixelOffset(to, distanceBetween / 3, controlPointHeading2.toRadians())
        return PixelControlPoints(pA, pB)
    }
}
