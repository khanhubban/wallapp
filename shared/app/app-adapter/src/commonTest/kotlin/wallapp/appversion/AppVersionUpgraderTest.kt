package wallapp.appversion

import wallapp.appstate.AppState
import wallapp.buildconfig.BuildConfig


class AppVersionUpgraderTest(
    buildConfig: BuildConfig,
    appState: AppState,
) : AppVersionUpgrader(
    buildConfig, appState,
) {

    var upgradeCalled: Boolean = false

    override fun onUpgrade(fromVersionCode: Long, toVersionCode: Long) {
        upgradeCalled = true
    }
}