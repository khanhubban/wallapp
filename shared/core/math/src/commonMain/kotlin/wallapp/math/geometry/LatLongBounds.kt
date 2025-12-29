package wallapp.math.geometry

data class LatLongBounds(
    val bounds: List<LatLong>,
) {
    val extremePoints: Pair<LatLong, LatLong> by lazy { bounds.extremePoints }
}