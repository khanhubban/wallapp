package wallapp.system.version

import android.os.Build

object SystemVersionAndroid : SystemVersion {
    
    override val versionId: String by lazy {
        val osRelease = Build.VERSION.RELEASE
        val securityPatch = Build.VERSION.SECURITY_PATCH
        "${osRelease}_${securityPatch}"
    }
}