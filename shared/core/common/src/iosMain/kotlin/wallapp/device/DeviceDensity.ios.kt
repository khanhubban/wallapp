package wallapp.device

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.UIKit.UIScreen


@OptIn(ExperimentalForeignApi::class)
object DeviceSpecIos : DeviceSpec {

    private val screenScale: Float
        get() = UIScreen.mainScreen.scale.toFloat()

    override val density: StateFlow<Float> by lazy {
        MutableStateFlow(screenScale)
    }
    override val size: StateFlow<DeviceSize> by lazy {
        val width = UIScreen.mainScreen.bounds.useContents { this.size.width }
        val height = UIScreen.mainScreen.bounds.useContents { this.size.height }
        MutableStateFlow(DeviceSize((width * screenScale).toInt(), (height * screenScale).toInt()))
    }
}