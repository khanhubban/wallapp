package wallapp.pixel.bottomsheet

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wallapp.pixel.bottomsheet3.BottomSheetScaffoldState


class BottomSheetManagerCompose(
    override val descriptor: BottomSheetDescriptor,
    val bottomSheetScaffoldState: BottomSheetScaffoldState,
    val coroutineScope: CoroutineScope,
) : BottomSheetManager {

    private val _state = MutableStateFlow(BottomSheetState.Collapsed)
    override val state: StateFlow<BottomSheetState>
        get() = _state

    override fun setState(state: BottomSheetState) {
        when (state) {
            BottomSheetState.Expanded -> {
                setExpanded()
            }
            BottomSheetState.HalfExpanded -> {
                TODO("Add support")
//                setHalfExpanded()
            }
            BottomSheetState.Collapsed -> {
                setCollapsed()
            }
            else -> {}
        }
    }

    private val _stateChange = MutableStateFlow<BottomSheetStateChange?>(null)
    override val stateChange: MutableStateFlow<BottomSheetStateChange?>
        get() = _stateChange

    private fun setExpanded() {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }

//    private fun setHalfExpanded() {
//        coroutineScope.launch {
//            bottomSheetScaffoldState.bottomSheetState.halfExpand()
//        }
//    }

    private fun setCollapsed() {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.collapse()
        }
    }

    private val bottomSheetStateEx
        get() = bottomSheetScaffoldState.bottomSheetState

    /**
     * Called as the [BottomSheetScaffoldEx] content is recomposed.
     */
    val bottomSheetOnUpdate: () -> Unit = {
        val bottomSheetState = bottomSheetStateEx.inferBottomSheetState()
        val stateChange = bottomSheetStateEx.asStateChange
        if (bottomSheetState != state.value || stateChange != _stateChange.value) {
            setState(bottomSheetState)
            _stateChange.value = stateChange
        }
//        Log.v(bottomSheetStateEx.debugString)
    }

    override fun addListener(listener: BottomSheetListener) { }

    override fun removeListener(listener: BottomSheetListener) { }
}