package wallapp.image.scaler

import wallapp.device.DeviceSpec

class ImageScalerDeviceSpec(
    private val deviceSpec: DeviceSpec,
) : ImageScaler() {

    override val density: Float
        get() = deviceSpec.density.value

    override val widthScale: Float
        get() = 1f
    override val heightScale: Float
        get() = 1f
}