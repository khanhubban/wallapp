package wallapp.appstate

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
import wallapp.time.TimeRepository
import wallapp.time.TimeRepositoryMock

class AppStateStub(
    preferenceDefaults: PreferenceDefaults = PreferenceDefaultsMock,
    deviceSharedPreferences: Settings = SettingsMemory("AppStateStub/deviceSharedPreferences"),
    timeRepository: TimeRepository = TimeRepositoryMock(),
    processBridgePreferenceManager: ProcessBridgePreferenceManager = ProcessBridgePreferenceManagerStub(),
    flavorConfig: FlavorConfig = FlavorConfigStub(),
    coroutineScope: CoroutineScope = CoroutineScopeDefault,
) : AppStateAltProcess(
    preferenceDefaults,
    deviceSharedPreferences,
    timeRepository,
    processBridgePreferenceManager,
    flavorConfig,
    coroutineScope,
)
