package wallapp.view

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import wallapp.image.bucket.ImageBucketManager
import wallapp.system.window.WindowFrame
import wallapp.system.window.WindowFrameManager
import wallapp.util.combine

class ViewSpecArbitratorConfigDefault(
    private val windowFrameManager: WindowFrameManager,
    private val imageBucketManager: ImageBucketManager,
    private val coroutineScopeMainImmediate: CoroutineScope,
) : ViewSpecArbitratorConfig {

    override val isReady: StateFlow<Boolean> by lazy {
        combine(
            windowFrameManager.isReady,
            imageBucketManager.currentImageBucketSpec
        ) { windowFrameIsReady, currentImageBucketSpec ->
            windowFrameIsReady && currentImageBucketSpec != null
        }.stateIn(coroutineScopeMainImmediate, started = SharingStarted.Eagerly, false)
    }

    private val windowFrameIsReady: Boolean
        get() = windowFrameManager.isReady.value
    val windowFrame: WindowFrame
        get() = requireNotNull(windowFrameManager.windowFrame.value)
    override val windowWidth: Dp
        get() = windowFrame.deviceWidth.also {
            require(windowFrameIsReady) {"windowFrame is not ready"}
        }
    override val windowHeight: Dp
        get() = windowFrame.deviceHeight.also {
            require(windowFrameIsReady) {"windowFrame is not ready"}
        }

    override val statusBarHeight: Dp
        get() = windowFrame.statusBarHeight
    override val navigationBarHeight: Dp
        get() = windowFrame.navigationBarHeight

    override val feedColumns: Int
        get() = 2
}