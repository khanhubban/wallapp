package wallapp.pixel.bottomsheet

/**
 * The position of the bottom sheet. Not to be confused with [BottomSheetState], which can include
 * intermediate states such as dragging, settling, etc.
 */
sealed class BottomSheetPosition {

    data object Collapsed : BottomSheetPosition()

    data object HalfExpanded : BottomSheetPosition()

    data object ExpandedState : BottomSheetPosition()

    data object PreviousUserPosition : BottomSheetPosition()
}

fun BottomSheetPosition.asBottomSheetState(
    previousUserBottomSheetState: BottomSheetState? = null,
): BottomSheetState? {
    return when (this) {
        BottomSheetPosition.Collapsed -> BottomSheetState.Collapsed
        BottomSheetPosition.HalfExpanded -> BottomSheetState.HalfExpanded
        BottomSheetPosition.ExpandedState -> BottomSheetState.Expanded
        BottomSheetPosition.PreviousUserPosition -> previousUserBottomSheetState
    }
}