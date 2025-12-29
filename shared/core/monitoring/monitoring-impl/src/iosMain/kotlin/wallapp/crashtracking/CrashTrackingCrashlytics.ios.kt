package wallapp.crashtracking

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import wallapp.annotation.CheckResult
import wallapp.log.Logger

class CrashTrackingCrashlyticsIos(
    crashTrackingUserId: CrashTrackingUserId,
    coroutineScopeMain: CoroutineScope,
) : CrashTracking {

    companion object {
        val Log = Logger("CrashTracking")
    }

    @CheckResult override fun logFatalException(exception: Exception, message: String): Exception {
        Log.e(exception, exception.message)
        log(message, true)
        throw exception
    }

    override fun logNonFatalException(exception: Exception) {
        Log.w(exception, exception.message)

        CrashlyticsKotlin.sendHandledException(exception)
    }

    override fun logNonFatalThrowable(throwable: Throwable) {
        Log.w(throwable, throwable.message)

        CrashlyticsKotlin.sendHandledException(throwable)
    }

    override fun log(message: String, logToConsole: Boolean) {
        if (logToConsole) {
            Log.i(message)
        }
        CrashlyticsKotlin.logMessage(message)
    }

    private fun initialize(): Boolean {
        Log.d("CrashKiosInitializerIos initialize() start")
        // Catch/trap any Exception/Error initializing CrashKios/Crashlytics. Not ideal, but
        // there have been crash reports of initializing failing, and it's never something that
        // can justify causing the app to crash. #1193.
        return try {
            // https://crashkios.touchlab.co/docs/crashlytics#step-2---add-crashkios
            enableCrashlytics()
            setCrashlyticsUnhandledExceptionHook()
            Log.d("CrashKiosInitializerIos initialize() complete")
            true
        } catch (e: Exception) {
            Log.e(e, "CrashKiosInitializerIos initialize() failed")
            false
        } catch (e: Error) {
            Log.e(e, "CrashKiosInitializerIos initialize() failed")
            false
        }
    }

    init {
        Log.d("Initializing...")

        if (initialize()) {
            crashTrackingUserId.crashTrackingUserId
                .onEach { userId ->
                    if (userId != null) {
                        Log.d("setUserId(): $userId")
                        CrashlyticsKotlin.setUserId(userId)
                    } else {
                        Log.d("clearUserId()")
                        CrashlyticsKotlin.setUserId("")
                    }
                }
                .launchIn(coroutineScopeMain)
        }
    }
}