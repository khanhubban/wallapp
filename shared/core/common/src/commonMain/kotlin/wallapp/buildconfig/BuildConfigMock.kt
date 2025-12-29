package wallapp.buildconfig

import wallapp.appversion.AppVersion
import wallapp.appversion.AppVersion.AppVersionAndroid

class BuildConfigMock : BuildConfig {
    var _debug = true
    override val debug: Boolean
        get() = _debug

    override var appVersion: AppVersion = AppVersionAndroid(
        versionName = "1.0",
        versionCode = 100,
    )

    var _packageName = ""
    override val packageName: String
        get() = _packageName

    var _appName = ""
    override val appName: String
        get() = _appName
}