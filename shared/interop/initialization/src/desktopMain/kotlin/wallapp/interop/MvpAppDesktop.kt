package wallapp.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import wallapp.account.SignInMethod
import wallapp.di.FactoryDesktop
import wallapp.log.Log
import wallapp.log.LogEmitterDesktop
import wallapp.remoteendpoint.RemoteEndpointMode
import wallapp.result.ResultEx
import wallapp.runmode.RunMode

fun MvpAppDesktop(
    allModules: List<Module>,
): InteropModulesDesktop {
    registerInteropBridge(InteropBridgeDesktop)
    Log.registerEmitter(LogEmitterDesktop())
    MvpApp.appInitialize(
        allModules = allModules,
        interopBridge = InteropBridgeDesktop,
        isDebug = null,
        runMode = RunMode.App,
    )
    val interopModules = InteropModulesDesktop

    require(FactoryDesktop.remoteEndpointMode == RemoteEndpointMode.Bundled
            || FactoryDesktop.remoteEndpointMode == RemoteEndpointMode.FirebaseAdmin) {
        "RemoteEndpointMode must be FirebaseAdmin or Bundled for MvpAppDesktop"
    }

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val accountManager = interopModules.accountManager
    val appLifecycleManager = interopModules.appLifecycleManager
    val networkErrorBroadcaster = interopModules.networkErrorBroadcaster
    val networkRefreshManager = interopModules.networkRefreshManager
    val networkRefreshTriggerBroadcaster = interopModules.networkRefreshTriggerBroadcaster

    networkRefreshManager.initialize()
    appLifecycleManager.appCameToForeground()

    appScope.launch {
       networkRefreshTriggerBroadcaster.userSignInRefresh.collectLatest {
           Log.d("[NRW] userSignInRefresh: $it")
           when (val result = accountManager.signIn(SignInMethod.Anonymous)) {
               is ResultEx.Error -> {
                   Log.w(result.exception, "[firebase] ${result.exception.message}")
                   Log.w("[firebase] signInAnonymously: failure")
                   networkErrorBroadcaster.reportNetworkError("signInAnonymously")
               }
               is ResultEx.Success -> {
                   Log.d("[firebase] signInAnonymously: success")
                   appLifecycleManager.appCameToForeground()
               }
           }
       }
    }

    return interopModules
}