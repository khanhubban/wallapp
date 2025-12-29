package wallapp.settings

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import wallapp.preference.PreferenceInfo
import wallapp.settings.SettingFactoryCommon.mutableSetting
import wallapp.settings.SettingFactoryCommon.mutableSettingFlow
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingFactoryCommonTest {

    @Test fun `mutableSettingFlow - default PreferenceInfo value is set as default MutableStateFlow value`() = runTest {
        val settings = SettingsMultiplatform(MapSettings())
        val preferenceInfo = PreferenceInfo("key", -99)
        val updateDispatcher = SettingsUpdateDispatcher(settings)
        val coroutineScope = TestScope(UnconfinedTestDispatcher())

        val mutableSettingFlow = mutableSettingFlow(preferenceInfo, settings, updateDispatcher, coroutineScope)

        assertEquals(-99, mutableSettingFlow.value)
    }

    @Test fun `mutableSettingFlow - updated MutableStateFlow value is propagated to settings`() = runTest {
        val settings = SettingsMultiplatform(MapSettings())
        val preferenceInfo = PreferenceInfo("key", -99)
        val updateDispatcher = SettingsUpdateDispatcher(settings)
        val coroutineScope = TestScope(UnconfinedTestDispatcher())
        val mutableSettingFlow = mutableSettingFlow(preferenceInfo, settings, updateDispatcher, coroutineScope)
        assertEquals(-99, mutableSettingFlow.value)

        mutableSettingFlow.value = 1
        assertEquals(1, settings.getInt("key", -1))
    }

    @Test fun `mutableSetting - updated MutableStateFlow value is propagated to settings`() = runTest {
        val settings = SettingsMultiplatform(MapSettings())
        val preferenceInfo = PreferenceInfo("key", -99)
        val updateDispatcher = SettingsUpdateDispatcher(settings)
        val coroutineScope = TestScope(UnconfinedTestDispatcher())
        val mutableSettingFlow = mutableSetting(preferenceInfo, settings, updateDispatcher, coroutineScope)
        assertEquals(-99, mutableSettingFlow.value)

        mutableSettingFlow.update(1)
        assertEquals(1, settings.getInt("key", -1))
    }

}