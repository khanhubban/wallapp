package wallapp.pixel.bottomsheet

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class BottomSheetDescriptor(
    val peekHeight: Dp,
    val peekOffset: Dp,             // Navigation bar height
    val halfExpandedHeight: Dp?,    // Setting null disables HalfExpanded state
    val expandedOffset: Dp,
) {

    companion object {
        val Preset = BottomSheetDescriptor(
            peekHeight = 88.dp,
            peekOffset = 24.dp,
            halfExpandedHeight = null,
            expandedOffset = 0.dp, //BottomSheetScaffoldDefaults.SheetExpandedOffset.dp,
        )
    }
}