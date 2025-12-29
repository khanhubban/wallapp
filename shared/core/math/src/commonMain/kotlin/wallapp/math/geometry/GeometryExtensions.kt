/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package wallapp.math.geometry


fun vectorBetween(from: Point3D, to: Point3D): Vector {
    return Vector(
        to.x - from.x,
        to.y - from.y,
        to.z - from.z
    )
}

fun intersects(sphere: Sphere, ray: Ray): Boolean {
    return distanceBetween(sphere.center, ray) < sphere.radius
}

// http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
// Note that this formula treats Ray as if it extended infinitely past
// either point.
fun distanceBetween(point: Point3D, ray: Ray): Float {
    val p1ToPoint = vectorBetween(ray.point, point)
    val p2ToPoint = vectorBetween(
        ray.point.translate(ray.vector),
        point
    )

    // The length of the cross product gives the area of an imaginary
    // parallelogram having the two vectors as sides. A parallelogram can be
    // thought of as consisting of two triangles, so this is the same as
    // twice the area of the triangle defined by the two vectors.
    // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
    val areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length()
    val lengthOfBase = ray.vector.length()

    // The area of a triangle is also equal to (base * height) / 2. In
    // other words, the height is equal to (area * 2) / base. The height
    // of this triangle is the distance from the point to the ray.
    return areaOfTriangleTimesTwo / lengthOfBase
}

// http://en.wikipedia.org/wiki/Line-plane_intersection
// This also treats rays as if they were infinite. It will return a
// point full of NaNs if there is no intersection point.
fun intersectionPoint(ray: Ray, plane: Plane): Point3D {
    val rayToPlaneVector =
        vectorBetween(ray.point, plane.point)

    val scaleFactor =
        rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal)

    return ray.point.translate(ray.vector.scale(scaleFactor))
}
