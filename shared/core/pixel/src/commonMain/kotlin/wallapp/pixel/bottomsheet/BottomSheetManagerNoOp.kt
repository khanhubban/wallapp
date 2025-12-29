package wallapp.pixel.bottomsheet

import kotlinx.coroutines.flow.MutableStateFlow


class BottomSheetManagerNoOp(
    override val descriptor: BottomSheetDescriptor = BottomSheetDescriptor.Preset,
    override val state: MutableStateFlow<BottomSheetState> = MutableStateFlow(BottomSheetState.Collapsed),
    override val stateChange: MutableStateFlow<BottomSheetStateChange?> = MutableStateFlow(null),
) : BottomSheetManager {

    override fun setState(state: BottomSheetState) { }

    override fun addListener(listener: BottomSheetListener) { }

    override fun removeListener(listener: BottomSheetListener) { }
}