package wallapp.math.geometry

data class Point3D(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {

    fun translateY(distance: Float): Point3D {
        return Point3D(x, y + distance, z)
    }

    fun translate(vector: Vector): Point3D {
        return Point3D(
            x + vector.x,
            y + vector.y,
            z + vector.z
        )
    }
}