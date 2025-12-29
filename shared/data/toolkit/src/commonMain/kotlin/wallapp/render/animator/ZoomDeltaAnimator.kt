package wallapp.render.animator


interface ZoomDeltaAnimator {

    /**
     * Only returns true if any animator is running on the zoomDelta values.
     */
    val isAnimating: Boolean

    /**
     * The absolute value of rotationDelta without any info of rotation's direction.
     */
    val rotationDelta: Float

    /**
     * Called when an updated [zoomDelta] value is available.
     *
     * Returns [rotationDelta].
     */
    fun update(zoomDelta: Float): Float

    /**
     * Should be called as appropriate when [isAnimating] is [true].
     *
     * Returns [rotationDelta].
     */
    fun tick(): Float

    /**
     * Cancels any animator that is currently running.
     */
    fun cancelAnimation()
}