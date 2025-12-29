package wallapp.di

import org.koin.test.check.checkModules
import wallapp.initializer.app.appInitializeTest
import kotlin.test.Test

class ModulesMvpTestDesktop {

    @Test
    fun checkAllModules() {
        val diApplication: DiApplication = appInitializeTest()
        require(diApplication is DiApplicationKoin)
        val koinApplication = diApplication.koin
        koinApplication.checkModules()
//        appModule.verify()
    }
}