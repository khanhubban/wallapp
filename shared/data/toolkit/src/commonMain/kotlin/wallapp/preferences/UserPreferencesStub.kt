package wallapp.preferences

import kotlinx.coroutines.CoroutineScope
import wallapp.coroutine.CoroutineScopeDefault
import wallapp.prefs.PreferenceDefaults
import wallapp.prefs.PreferenceDefaultsMock
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.process.preference.ProcessBridgePreferenceManagerStub
import wallapp.settings.Settings
import wallapp.settings.SettingsMemory


class UserPreferencesStub(
    preferenceDefaults: PreferenceDefaults = PreferenceDefaultsMock,
    userSettings: Settings = SettingsMemory("UserPreferencesStub/userSettings"),
    processBridgePreferenceManager: ProcessBridgePreferenceManager = ProcessBridgePreferenceManagerStub(),
    coroutineScope: CoroutineScope = CoroutineScopeDefault,
) : UserPreferencesAltProcess(
    preferenceDefaults,
    userSettings,
    processBridgePreferenceManager,
    coroutineScope,
)
