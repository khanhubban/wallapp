package wallapp.crashtracking


object CrashTrackingArbitrated : CrashTracking {

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    override fun logFatalException(exception: Exception, message: String): Exception {
        return crashTracking.logFatalException(exception, message)
    }

    override fun logNonFatalException(exception: Exception) {
        crashTracking.logNonFatalException(exception)
    }

    override fun logNonFatalThrowable(throwable: Throwable) {
        crashTracking.logNonFatalThrowable(throwable)
    }

    override fun log(message: String, logToConsole: Boolean) {
        crashTracking.log(message, logToConsole)
    }
}