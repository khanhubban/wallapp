package wallapp.pixel.bottomsheet

/**
 * Note: [from] and [to] will be the same when [progressFraction] == 1.0.
 */
data class BottomSheetStateChange(
    /**
     * Fraction of the animation progress. 0.0 means [from], 1.0 means [to].
     */
    val progressFraction: Float,
    val from: BottomSheetState,
    val to: BottomSheetState,
    /**
     * Fraction of progress to being open. 0.0 means closed, 1.0 means fully open.
     */
    val openProgressFraction: Float,
)
