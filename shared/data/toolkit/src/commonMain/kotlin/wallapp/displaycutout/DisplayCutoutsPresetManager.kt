package wallapp.displaycutout

import wallapp.device.DeviceModel

interface DisplayCutoutsPresetManager {

    val displayCutoutPreset: DisplayCutoutsPreset?

}

class DisplayCutoutsPresetManagerNoOp : DisplayCutoutsPresetManager {

    override val displayCutoutPreset: DisplayCutoutsPreset?
        get() = null
}

class DisplayCutoutsPresetManagerDefault(
    private val deviceModel: DeviceModel,
) : DisplayCutoutsPresetManager {

    override val displayCutoutPreset: DisplayCutoutsPreset?
        get() = findDisplayCutoutsPreset(deviceModel)
}