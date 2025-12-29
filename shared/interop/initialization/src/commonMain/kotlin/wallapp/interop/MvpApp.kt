package wallapp.interop

import org.koin.core.module.Module
import wallapp.app.AppUiState
import wallapp.app.AppViewModel
import wallapp.app.resolveAppViewModel
import wallapp.di.resolveDependency
import wallapp.initializer.app.AppInitializerDefault
import wallapp.initializer.app.appInitialize
import wallapp.monitoring.MonitoringManager
import wallapp.runmode.RunMode

object MvpApp {

    fun appInitialize(
        allModules: List<Module>,
        interopBridge: Any,
        isDebug: Boolean?,
        runMode: RunMode,
    ) {
        appInitialize(
            appInitializer = AppInitializerDefault(
                application = null,
                allModules = allModules,
                interopBridge = interopBridge,
                multiProcessAllowed = false,
                isDebug = isDebug,
                runMode = runMode,
            ) { }
        )
        monitoringManager.initialize()
    }

    private val monitoringManager: MonitoringManager by lazy { resolveDependency() }
    private val appViewModel: AppViewModel by lazy { resolveAppViewModel() }

    fun createAppUiState(): AppUiState {
        return AppUiState(
            appViewModel = appViewModel,
            themeManager = resolveDependency(),
        )
    }
}