package wallapp.view

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.StateFlow
import wallapp.image.bucket.ImageBucketSpecs
import wallapp.system.window.WindowFrame

interface ViewSpecArbitratorConfig {

    val isReady: StateFlow<Boolean>

    val windowWidth: Dp
    val windowHeight: Dp

    // Is the current window size considered compact? An example might be the iPhone SE.
    val windowIsCompact: Boolean
        get() {
            return windowHeight <= WindowFrame.PresetCompat.windowSize.deviceHeight
        }

    val statusBarHeight: Dp
    val navigationBarHeight: Dp

    val feedColumns: Int
}

fun ViewSpecArbitratorConfigMock(): ViewSpecArbitratorConfig =
    ViewSpecArbitratorConfigImageBucketSpec(ImageBucketSpecs.PhoneAppleNormal)