package wallapp.application

import android.content.res.Configuration
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import timber.log.Timber
import wallapp.account.RUN_FIREBASE_ON_LOCAL_EMULATORS
import wallapp.app.android.BuildConfig
import wallapp.context.toast
import wallapp.crashtracking.CrashTrackingHolder.crashTracking
import wallapp.di.module.AllModules
import wallapp.initializer.app.AppInitializerDefault
import wallapp.initializer.app.appInitialize
import wallapp.interop.InteropBridgeAndroid
import wallapp.lifecycle.AppLifecycleManager
import wallapp.log.Log
import wallapp.log.LogEmitterAndroid
import wallapp.log.LogEmitterNoOp
import wallapp.runmode.RunMode
import wallapp.worker.WorkerJobIds
import android.app.Application as SystemApplication

class MainApplication : SystemApplication(), androidx.work.Configuration.Provider {

    private val application: Application by inject()
    private val appLifecycleManager: AppLifecycleManager by inject()
    private val applicationAndroid: ApplicationAndroid
        get() = application as ApplicationAndroid

    companion object {

    }

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Log.registerEmitter(LogEmitterAndroid())
        } else {
            Log.registerEmitter(LogEmitterNoOp)
        }

        if (RUN_FIREBASE_ON_LOCAL_EMULATORS) {
            setupFirebaseForTesting()
        }

        appInitialize(
            appInitializer = AppInitializerDefault(
                allModules = AllModules,
                application = this,
                interopBridge = InteropBridgeAndroid,
                multiProcessAllowed = false,
                isDebug = BuildConfig.DEBUG,
                runMode = RunMode.App,
            ) {
                androidLogger()
                androidContext(this@MainApplication)
            }
        )

        applicationAndroid.onCreatePre()

        super.onCreate()

        applicationAndroid.onCreatePost()

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    appLifecycleManager.appCameToForeground()
                }

                override fun onPause(owner: LifecycleOwner) {
                    appLifecycleManager.appWentToBackground()
                }
            }
        )
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        Log.w("[stability] onTrimMemory(), level=%d", level)

        applicationAndroid.onTrimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()

        crashTracking.log("[stability] onLowMemory")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        applicationAndroid.onConfigurationChanged()
    }

    override val workManagerConfiguration: androidx.work.Configuration
        get() = androidx.work.Configuration.Builder()
            .setJobSchedulerJobIdRange(WorkerJobIds.MinJobId, WorkerJobIds.MaxJobId)
            .build()

    private fun setupFirebaseForTesting() {
        if (wallapp.app.adapter.BuildConfig.DEBUG) {
            // 10.0.2.2 is the special IP address to connect to the 'localhost' of
            // the host computer from an Android emulator.
            val localhost = "10.0.2.2"
            Firebase.auth.useEmulator(localhost, 9099)
            Firebase.firestore.useEmulator(localhost, 8080)
            Firebase.storage.useEmulator(localhost, 9199)
            toast("Firebase emulator enabled")
        }
    }
}
