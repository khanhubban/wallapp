package wallapp.ui.content.toolbar

fun arbitrateToolbarContentAnimatedAlpha(animationProgress: Float): Float {
    val startStep = .99f
    val endStep = .7f
    val animationRange = startStep - endStep
    return if (animationProgress > startStep) {
        1f
    } else if (animationProgress < endStep) {
        0f
    } else {
        (animationProgress - endStep) / animationRange
    }
}
