package wallapp.appconfig

import kotlinx.coroutines.CoroutineScope
import wallapp.coroutine.CoroutineScopeDefault
import wallapp.flavorconfig.FlavorConfig
import wallapp.flavorconfig.FlavorConfigStub
import wallapp.prefs.PreferenceDefaults
import wallapp.prefs.PreferenceDefaultsMock
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.process.preference.ProcessBridgePreferenceManagerStub
import wallapp.settings.Settings
import wallapp.settings.SettingsMemory


class AppConfigStub(
    preferenceDefaults: PreferenceDefaults = PreferenceDefaultsMock,
    userSettings: Settings = SettingsMemory("AppConfigStub/userSettings"),
    deviceSettings: Settings = SettingsMemory("AppConfigStub/deviceSettings"),
    processBridgePreferenceManager: ProcessBridgePreferenceManager = ProcessBridgePreferenceManagerStub(),
    coroutineScope: CoroutineScope = CoroutineScopeDefault,
    flavorConfig: FlavorConfig = FlavorConfigStub(),
) : AppConfigAltProcess(
    preferenceDefaults,
    userSettings,
    deviceSettings,
    processBridgePreferenceManager,
    coroutineScope,
    flavorConfig,
)