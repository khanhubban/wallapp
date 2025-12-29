package wallapp.di

import android.app.Application
import android.os.Build
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import wallapp.initializer.app.appInitializeTest
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class, sdk = [Build.VERSION_CODES.O_MR1])
class ModulesMvpTestAndroid : KoinTest {

    @Test
    fun checkAllModules() {
        val koinApplication = appInitializeTest()
        koinApplication.checkModules()
//        appModule.verify()
    }
}