package wallapp.pixel.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import wallapp.pixel.bottomsheet3.BottomSheetValue
import wallapp.pixel.bottomsheet3.BottomSheetState as BottomSheetStateMaterial


//fun BottomSheetState.asBottomSheetBehaviorState(): Int =
//    when (this) {
//        BottomSheetState.Expanded -> BottomSheetBehavior.STATE_EXPANDED
//        BottomSheetState.Collapsed -> BottomSheetBehavior.STATE_COLLAPSED
//        BottomSheetState.Dragging -> BottomSheetBehavior.STATE_DRAGGING
//        BottomSheetState.Settling -> BottomSheetBehavior.STATE_SETTLING
//        BottomSheetState.Hidden -> BottomSheetBehavior.STATE_HIDDEN
//        BottomSheetState.HalfExpanded -> BottomSheetBehavior.STATE_HALF_EXPANDED
//    }
//
//fun asBottomSheetState(bottomSheetBehaviorState: Int): BottomSheetState =
//    when (bottomSheetBehaviorState) {
//        BottomSheetBehavior.STATE_EXPANDED -> BottomSheetState.Expanded
//        BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetState.Collapsed
//        BottomSheetBehavior.STATE_DRAGGING -> BottomSheetState.Dragging
//        BottomSheetBehavior.STATE_SETTLING -> BottomSheetState.Settling
//        BottomSheetBehavior.STATE_HIDDEN -> BottomSheetState.Hidden
//        BottomSheetBehavior.STATE_HALF_EXPANDED -> BottomSheetState.HalfExpanded
//        else -> throw IllegalArgumentException("Unhandled state: $bottomSheetBehaviorState")
//    }

val BottomSheetValue.bottomSheetState: BottomSheetState
    get() = when (this) {
        BottomSheetValue.Expanded -> BottomSheetState.Expanded
        BottomSheetValue.Collapsed -> BottomSheetState.Collapsed
    }


@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheetStateMaterial.inferBottomSheetState(): BottomSheetState {
    if (currentValue == targetValue) {
        return currentValue.bottomSheetState
    }

    // TODO: differentiate between drag and settle
    return BottomSheetState.Settling
}

@OptIn(ExperimentalMaterial3Api::class)
val BottomSheetStateMaterial.asStateChange: BottomSheetStateChange?
    get() {
        if (currentValue == targetValue) {
            return null
        }

        val fromState = progress.from.bottomSheetState
        val toState = progress.to.bottomSheetState

        val openProgressFraction = when (toState) {
            BottomSheetState.Expanded -> progress.fraction
            BottomSheetState.Collapsed -> 1f - progress.fraction
            else -> 0f
        }

        return BottomSheetStateChange(
            progressFraction = progress.fraction,
            from = fromState,
            to = toState,
            openProgressFraction = openProgressFraction,
        )
    }

@OptIn(ExperimentalMaterial3Api::class)
val BottomSheetStateMaterial.debugString: String
    get() {
        val direction = direction
        val currentValue: BottomSheetValue = currentValue
        val targetValue: BottomSheetValue = targetValue
        val overflow = overflow.value
        val offset = offset.value

        val fraction = progress.fraction
        val from = progress.from.name
        val to = progress.to.name

        return "direction: $direction, currentValue: $currentValue, targetValue: $targetValue, " +
                "overflow: $overflow, offset: $offset, progress: $progress, fraction: $fraction, " +
                "from: $from, to: $to"
    }