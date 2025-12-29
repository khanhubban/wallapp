package wallapp.math.geometry

data class Circle(val center: Point3D, val radius: Float) {

    fun scale(scale: Float): Circle {
        return Circle(center, radius * scale)
    }
}