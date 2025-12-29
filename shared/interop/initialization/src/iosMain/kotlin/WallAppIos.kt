@file:Suppress("unused")

import org.koin.core.module.Module
import wallapp.application.ApplicationIos
import wallapp.di.resolveDependency
import wallapp.interop.InteropBridge
import wallapp.interop.InteropFactory
import wallapp.interop.MvpApp
import wallapp.interop.registerInteropFactory
import wallapp.log.Log
import wallapp.log.LogEmitterIos
import wallapp.log.LogEmitterNoOp
import wallapp.runmode.RunMode


private var isInitialized = false

fun initializeApp(
    allModules: List<Module>,
    interopFactory: InteropFactory,
    interopBridge: InteropBridge,
    isDebug: Boolean,
) {
    require(!isInitialized) { "MvpApp already initialized" }

    Log.registerEmitter(if (isDebug) { LogEmitterIos() } else { LogEmitterNoOp })
    Log.d("initializeMvpApp isDebug=$isDebug")

    registerInteropFactory(interopFactory)

    MvpApp.appInitialize(allModules, interopBridge, isDebug, RunMode.App)
    resolveDependency<ApplicationIos>()
    isInitialized = true
}

