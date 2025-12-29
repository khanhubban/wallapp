package wallapp.view

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.image.bucket.ImageBucketSpec

class ViewSpecArbitratorConfigImageBucketSpec(
    var imageBucketSpec: ImageBucketSpec,
    override val statusBarHeight: Dp = 24.dp,
    override val navigationBarHeight: Dp = 48.dp,
    override val feedColumns: Int = 2,
) : ViewSpecArbitratorConfig {
    override val isReady: StateFlow<Boolean> = MutableStateFlow(true)

    override val windowWidth: Dp
        get() = (imageBucketSpec.maxWidthPx / imageBucketSpec.maxDensity).dp
    
    override val windowHeight: Dp
        get() = (imageBucketSpec.maxHeightPx / imageBucketSpec.maxDensity).dp
}