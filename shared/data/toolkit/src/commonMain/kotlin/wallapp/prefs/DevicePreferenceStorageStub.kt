package wallapp.prefs

import kotlinx.coroutines.CoroutineScope
import wallapp.coroutine.CoroutineScopeDefault
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.process.preference.ProcessBridgePreferenceManagerStub
import wallapp.settings.Settings
import wallapp.settings.SettingsMemory

class DevicePreferenceStorageStub(
    settings: Settings = SettingsMemory("DevicePreferenceStorageStub/settings"),
    processBridgePreferenceManager: ProcessBridgePreferenceManager = ProcessBridgePreferenceManagerStub(),
    coroutineScope: CoroutineScope = CoroutineScopeDefault,
) : DevicePreferenceStorageDefault(
    PreferenceDefaultsMock,
    settings,
    processBridgePreferenceManager,
    coroutineScope,
)
