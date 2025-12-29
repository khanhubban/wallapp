package wallapp.image.bucket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.coroutine.collectIn
import wallapp.device.DeviceInfo
import wallapp.device.DeviceSpec
import wallapp.log.Log
import wallapp.system.window.WindowFrameManager
import wallapp.util.combine

class ImageBucketManagerDefault(
    windowFrameManager: WindowFrameManager,
    private val deviceInfo: DeviceInfo,
    deviceSpec: DeviceSpec,
    coroutineScopeMainImmediate: CoroutineScope,
) : ImageBucketManager {

    data class Data(
        val currentImageBucketSpec: ImageBucketSpec,
        val deviceImageBucketSpec: ImageBucketSpec,
    ) {
        val widthScaler: Float by lazy {
            currentImageBucketSpec.maxWidthPx.toFloat() / deviceImageBucketSpec.maxWidthPx
        }
        val heightScaler: Float by lazy {
            currentImageBucketSpec.maxHeightPx.toFloat() / deviceImageBucketSpec.maxHeightPx
        }
        val isSameSize: Boolean
            get() = widthScaler == 1f && heightScaler == 1f

        val debugString: String
            get() = "$currentImageBucketSpec, ${if (isSameSize) { "Exact bucket match!" } else { "widthScaler: $widthScaler, heightScaler: $heightScaler" } }"
    }

    private val data: Flow<Data?>
        = combine(
            windowFrameManager.windowFrame,
            deviceSpec.density,
        ) { windowFrame, density ->
            if (windowFrame == null) return@combine null

            val currentImageBucketSpec = ImageBucketArbitrator.arbitrateImageBucketSpec(
                deviceWidthPx = windowFrame.deviceWidthPx,
                deviceHeightPx = windowFrame.deviceHeightPx,
                deviceDensity = density,
            )
            val deviceImageBucketSpec = ImageBucketSpec(
                key = "<device>",
                label = deviceInfo.deviceModel.toString(),
                maxWidthPx = windowFrame.deviceWidthPx,
                maxHeightPx = windowFrame.deviceHeightPx,
                maxDensity = density,
            )
            Data(currentImageBucketSpec, deviceImageBucketSpec)
        }

    override val currentImageBucketSpec: StateFlow<ImageBucketSpec?> =
        data.map { it?.currentImageBucketSpec }
            .stateIn(coroutineScopeMainImmediate, started = SharingStarted.Eagerly, initialValue = null)

    override val currentImageBucketWidthScale: StateFlow<Float> =
        data.map { it?.widthScaler ?: 1f }
            .stateIn(coroutineScopeMainImmediate, started = SharingStarted.Eagerly, initialValue = 1f)

    override val currentImageBucketHeightScale: StateFlow<Float> =
        data.map { it?.heightScaler ?: 1f }
            .stateIn(coroutineScopeMainImmediate, started = SharingStarted.Eagerly, initialValue = 1f)

    init {
        data.collectIn(coroutineScopeMainImmediate) {
            if (it != null) {
                Log.d("ImageBucketManagerDefault Current image bucket: ${it.debugString}")
            }
        }
    }
}