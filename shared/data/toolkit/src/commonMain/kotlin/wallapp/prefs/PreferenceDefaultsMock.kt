package wallapp.prefs

import wallapp.system.version.SystemVersionNoOp

object PreferenceDefaultsMock : PreferenceDefaults(
    preferenceDefaultsProvider = PreferenceDefaultsProviderMock(),
    systemVersion = SystemVersionNoOp,
)
