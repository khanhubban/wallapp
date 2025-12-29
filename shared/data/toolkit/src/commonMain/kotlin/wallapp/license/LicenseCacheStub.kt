package wallapp.license

import kotlinx.coroutines.CoroutineScope
import wallapp.coroutine.CoroutineScopeDefault
import wallapp.prefs.PreferenceDefaults
import wallapp.prefs.PreferenceDefaultsMock
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.process.preference.ProcessBridgePreferenceManagerStub
import wallapp.settings.Settings
import wallapp.settings.SettingsMemory

class LicenseCacheStub(
    preferenceDefaults: PreferenceDefaults = PreferenceDefaultsMock,
    settings: Settings = SettingsMemory("LicenseCacheStub/settings"),
    processBridgePreferenceManager: ProcessBridgePreferenceManager = ProcessBridgePreferenceManagerStub(),
    coroutineScope: CoroutineScope = CoroutineScopeDefault,
) : LicenseCacheMainProcess(
    preferenceDefaults,
    settings,
    processBridgePreferenceManager,
    coroutineScope,
)
