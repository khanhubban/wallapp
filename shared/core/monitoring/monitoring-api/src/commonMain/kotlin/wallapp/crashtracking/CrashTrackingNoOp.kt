package wallapp.crashtracking

class CrashTrackingNoOp : CrashTracking {
    override fun logFatalException(exception: Exception, message: String): Exception {
        return exception
    }

    override fun logNonFatalException(exception: Exception) { }

    override fun logNonFatalThrowable(throwable: Throwable) { }

    override fun log(message: String, logToConsole: Boolean) { }
}