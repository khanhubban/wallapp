package wallapp.math.geometry

import android.graphics.Point as GraphicsPoint

val Point.graphicsPoint: GraphicsPoint
    get() = GraphicsPoint(x, y)
val GraphicsPoint.point: Point
    get() = Point(x, y)

