package wallapp.system.window

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import wallapp.log.Logger
import wallapp.system.unit.SystemUnitManager
import wallapp.util.combine

open class WindowFrameManagerDefault(
    private val systemUnitManager: SystemUnitManager,
    private val coroutineScopeMain: CoroutineScope,
) : WindowFrameManager {

    companion object {
        private val Log = Logger(tag = "WindowFrameManagerDefault")
    }

    private val _windowInsets: MutableStateFlow<WindowInsets?> = MutableStateFlow(null)
    private val _windowSize: MutableStateFlow<WindowSize?> = MutableStateFlow(null)

    private val _windowFrame: MutableStateFlow<WindowFrame?> = MutableStateFlow(null)
    override val windowFrame: StateFlow<WindowFrame?>
        get() = _windowFrame

    override val isReady: StateFlow<Boolean> by lazy {
        combine(_windowInsets, _windowSize) { insets, size ->
            insets != null && size != null
        }.stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, false)
    }

//    @Suppress("UNCHECKED_CAST")
//    private fun <T : Any> StateFlow<T?>.checkedNotNull(): StateFlow<T> {
//        return requireNotNull(this.value) { "Value is null - must call setInsets() or setSize()" }.let {
//            this as StateFlow<T>
//        }
//    }

    private fun updateWindowFrame() {
        val insets = _windowInsets.value
        val size = _windowSize.value
        if (insets != null && size != null) {
            _windowFrame.value = WindowFrame(
                windowInsets = insets,
                windowSize = size,
            ).also {
                Log.d("updateWindowFrame: $it")
            }
        }
    }

    fun setInsetsPx(statusBarHeight: Int, navBarHeight: Int) {
        setInsets(
            statusBarHeight = systemUnitManager.pxToDp(statusBarHeight),
            navBarHeight = systemUnitManager.pxToDp(navBarHeight),
        )
    }

    override fun setInsets(statusBarHeight: Dp, navBarHeight: Dp) {
        _windowInsets.value = WindowInsets(
            statusBarHeight = statusBarHeight,
            navigationBarHeight = navBarHeight,
        ).also {
            Log.i("setInsets(): $it")
        }
        updateWindowFrame()
    }

    open fun updateSize() {}

    override fun setSize(deviceWidthPx: Int, deviceHeightPx: Int) {
        _windowSize.value = WindowSize(
            deviceWidth = systemUnitManager.pxToDp(deviceWidthPx),
            deviceHeight = systemUnitManager.pxToDp(deviceHeightPx),
            deviceWidthPx = deviceWidthPx,
            deviceHeightPx = deviceHeightPx,
        ).also {
            Log.i("setSize(): $it")
        }
        updateWindowFrame()
    }

    fun setInsets(
        statusBarHeight: Dp,
        navBarHeight: Dp,
        deviceWidthPx: Int,
        deviceHeightPx: Int,
    ) {
        setSize(deviceWidthPx, deviceHeightPx)
        setInsets(statusBarHeight, navBarHeight)
    }
}