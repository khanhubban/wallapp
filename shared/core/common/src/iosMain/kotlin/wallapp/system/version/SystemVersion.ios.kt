package wallapp.system.version

import platform.UIKit.UIDevice


object SystemVersionIos : SystemVersion {

    override val versionId: String by lazy {
        UIDevice.currentDevice.systemVersion
    }
}
