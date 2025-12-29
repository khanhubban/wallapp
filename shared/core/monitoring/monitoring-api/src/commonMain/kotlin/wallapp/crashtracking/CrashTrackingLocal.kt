package wallapp.crashtracking

import wallapp.annotation.CheckResult
import wallapp.log.Log

object CrashTrackingLocal : CrashTracking {

    init {
        Log.d("%s, Initializing", this::class.simpleName)
    }

    @CheckResult override fun logFatalException(exception: Exception, message: String): Exception {
        Log.w(message)
        Log.e(exception, exception.message)
        return exception
    }

    override fun logNonFatalException(exception: Exception) {
        Log.w(exception, exception.message)
    }

    override fun logNonFatalThrowable(throwable: Throwable) {
        Log.w(throwable, throwable.message)
    }

    override fun log(message: String, logToConsole: Boolean) {
        Log.i(message)
    }
}