package wallapp.pixel.bottomsheet

import kotlinx.coroutines.flow.StateFlow

interface BottomSheetManager {

    val descriptor: BottomSheetDescriptor

    val state: StateFlow<BottomSheetState>
    fun setState(state: BottomSheetState)
    val stateChange: StateFlow<BottomSheetStateChange?>

    fun addListener(listener: BottomSheetListener)
    fun removeListener(listener: BottomSheetListener)
}