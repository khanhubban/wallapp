package wallapp.prefs

import kotlinx.coroutines.CoroutineScope
import wallapp.coroutine.CoroutineScopeDefault
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.process.preference.ProcessBridgePreferenceManagerStub
import wallapp.settings.Settings
import wallapp.settings.SettingsMemory

class PreferenceStorageStub(
    settings: Settings = SettingsMemory("PreferenceStorageStub/settings"),
    processBridgePreferenceManager: ProcessBridgePreferenceManager = ProcessBridgePreferenceManagerStub(),
    coroutineScope: CoroutineScope = CoroutineScopeDefault,
) : PreferenceStorageDefault(
    PreferenceDefaultsMock,
    settings,
    processBridgePreferenceManager,
    coroutineScope,
)
