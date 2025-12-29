package wallapp.displaycutout

import wallapp.device.DeviceModel
import wallapp.math.geometry.Rect


data class DisplayCutoutsPreset(
    val presetGroupLabel: String,
    val deviceModels: List<DeviceModel>,
    val expectedTopBounds: Rect,/** Only use [presetDisplayCutouts] if the system topBounds
                                        match this value. Ensures using Developer Options to test
                                        different cutout types on preset devices works. **/
    val presetDisplayCutouts: DisplayCutouts,
) {

    constructor(presetGroupLabel: String, deviceModel: DeviceModel, expectedTopBounds: Rect,
                presetDisplayCutouts: DisplayCutouts)
        : this(presetGroupLabel, listOf(deviceModel), expectedTopBounds, presetDisplayCutouts)

}