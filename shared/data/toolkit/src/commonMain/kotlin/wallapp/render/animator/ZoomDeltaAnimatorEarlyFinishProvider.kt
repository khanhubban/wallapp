package wallapp.render.animator

fun interface ZoomDeltaAnimatorEarlyFinishProvider {
    fun shouldFinish(rotationDelta: Float): Boolean
}