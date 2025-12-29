package wallapp.remotecontent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.preference.PreferenceInfo
import wallapp.settings.SettingFactory.mutableSettingFlow
import wallapp.settings.Settings
import wallapp.settings.SettingsUpdateDispatcher

class RemoteServerContentCacheDefault(
    private val settings: Settings,
    private val coroutineScopeIo: CoroutineScope,
) : RemoteServerContentCache {

    private val updateDispatcher = SettingsUpdateDispatcher(settings)

    override val endPoints: MutableStateFlow<String> = mutablePreferenceFlow(
        PreferenceInfo(
            key = "rE_123",
            default = ""
        )
    )

    override val baseContent: MutableStateFlow<String> = mutablePreferenceFlow(
        PreferenceInfo(
            key = "rE_124",
            default = ""
        )
    )

    override val mediaMap: MutableStateFlow<String> = mutablePreferenceFlow(
        PreferenceInfo(
            key = "rE_125",
            default = ""
        )
    )

    override val searchContent: MutableStateFlow<String> = mutablePreferenceFlow(
        PreferenceInfo(
            key = "rE_126",
            default = ""
        )
    )

    private inline fun <reified T : Any> mutablePreferenceFlow(preferenceInfo: PreferenceInfo<T>): MutableStateFlow<T> {
        return mutableSettingFlow(preferenceInfo, settings, updateDispatcher, coroutineScopeIo)
    }
}