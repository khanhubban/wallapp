package wallapp.displaycutout

import wallapp.device.DeviceModel
import wallapp.math.geometry.PointF
import wallapp.math.geometry.Rect


internal val displayCutoutsPresets = listOf(
    DisplayCutoutsPreset(
        presetGroupLabel = "Pixel 4a/5",
        deviceModels = listOf(
            DeviceModel("Google", "Pixel 5"),
            DeviceModel("Google", "Pixel 4a"),
            DeviceModel("Google", "Pixel 4a 5G"),
        ),
        expectedTopBounds = Rect(0f, 0f, 136f, 136f),
        presetDisplayCutouts = DisplayCutouts(
            topCutout = DisplayCutout(
                Rect(0f, 0f, 136f, 136f),
                PointF(83f, 77f),
            ),
            leftCutout = null,
            rightCutout = null,
        )
    ),
)

/**
 * Searches [displayCutoutsPresets] for a match against [deviceModel].
 *
 * Comparison checks:
 *   * Strip whitespace from device label.
 *   * Use lowercase labels.
 *   * Checks if the preset [DeviceModel] label is contained in [deviceModel]'s label, rather than
 *     requiring an exact match.
 */
fun findDisplayCutoutsPreset(deviceModel: DeviceModel): DisplayCutoutsPreset? {
    val deviceLabel = deviceModel.getLabel(stripWhitespace = true).lowercase()
    return displayCutoutsPresets.find { preset ->
        preset.deviceModels.find { model ->
            deviceLabel.contains(model.getLabel(stripWhitespace = true).lowercase())
        } != null
    }
}