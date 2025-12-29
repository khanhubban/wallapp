package wallapp.displaycutout

import wallapp.device.DeviceModel
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DisplayCutoutsPresetsKtTest {

    @Test fun `findDisplayCutoutsPreset() exact match`() {
        assertNotNull(findDisplayCutoutsPreset(DeviceModel("Google", "Pixel 5")))
        assertNotNull(findDisplayCutoutsPreset(DeviceModel("Google", "Pixel5")))
    }

    @Test fun `findDisplayCutoutsPreset() case insensitive match`() {
        assertNotNull(findDisplayCutoutsPreset(DeviceModel("Google", "Pixel4a")))
        assertNotNull(findDisplayCutoutsPreset(DeviceModel("Google", "Pixel4A")))
    }

    @Test fun `findDisplayCutoutsPreset() case insensitive prefix match`() {
        assertNotNull(findDisplayCutoutsPreset(DeviceModel("Google", "Pixel4a 5G")))
        assertNotNull(findDisplayCutoutsPreset(DeviceModel("Google", "Pixel4a 5G Verizon")))
    }

    @Test fun `findDisplayCutoutsPreset() unknown preset returns null`() {
        assertNull(findDisplayCutoutsPreset(DeviceModel("Google", "Pixel 99")))
    }

}