package wallapp.crashtracking

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import wallapp.annotation.CheckResult
import wallapp.log.Logger

class CrashTrackingCrashlyticsAndroid(
    crashTrackingUserId: CrashTrackingUserId,
    coroutineScopeMain: CoroutineScope,
) : CrashTracking {

    companion object {
        val Log = Logger("CrashTracking")
    }

    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

    @CheckResult override fun logFatalException(exception: Exception, message: String): Exception {
        log(message, true)
        Log.e(exception, exception.localizedMessage)
        throw exception
    }

    override fun logNonFatalException(exception: Exception) {
        Log.w(exception, exception.localizedMessage)
        crashlytics.recordException(exception)
    }

    override fun logNonFatalThrowable(throwable: Throwable) {
        Log.w(throwable, throwable.localizedMessage)
        crashlytics.recordException(throwable)
    }

    override fun log(message: String, logToConsole: Boolean) {
        crashlytics.log(message)
        if (logToConsole) {
            Log.i(message)
        }
    }

    init {
        Log.d("%s, Initializing", this::class.simpleName)

        crashTrackingUserId.crashTrackingUserId
            .onEach { userId ->
                if (userId != null) {
                    Log.d("setUserId(): $userId")
                    crashlytics.setUserId(userId)
                } else {
                    Log.d("clearUserId()")
                    crashlytics.setUserId("")
                }
            }
            .launchIn(coroutineScopeMain)
    }
}