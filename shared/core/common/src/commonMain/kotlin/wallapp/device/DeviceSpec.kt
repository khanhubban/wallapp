package wallapp.device

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface DeviceSpec {

    val density: StateFlow<Float>

    val size: StateFlow<DeviceSize>
}

object DeviceSpecNoOp : DeviceSpec {
    override val density: StateFlow<Float> = MutableStateFlow(1f)

    override val size: StateFlow<DeviceSize> = MutableStateFlow(DeviceSize(720, 1080))
}

data class DeviceSpecMock(
    override val density: StateFlow<Float> = MutableStateFlow(1f),
    override val size: StateFlow<DeviceSize> = MutableStateFlow(DeviceSize(720, 1080)),
) : DeviceSpec {

    constructor(density: Float) : this(MutableStateFlow(density))
}
