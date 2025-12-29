package wallapp.log

import kotlin.jvm.JvmName

class Logger(
    val tag: String? = null,
    var loggingEnabled: Boolean = true,
    var minLogLevel: LogLevel = LogLevel.Verbose,
) {
    private val tagPrefix = if (tag != null) "$tag: " else ""
    private val String.tagged: String
        @JvmName("loggedTagged") get() = "$tagPrefix$this"
    private val String?.tagged: String?
        @JvmName("loggedTaggedNullable") get() = this?.tagged

    private fun log(level: LogLevel): Boolean {
        return loggingEnabled && level.ordinal <= minLogLevel.ordinal
    }

    fun e(message: String, vararg args: Any?) {
        if (log(LogLevel.Error)) {
            Log.e(message.tagged, *args)
        }
    }
    fun e(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (log(LogLevel.Error)) {
            Log.e(t, message.tagged, *args)
        }
    }

    fun w(message: String, vararg args: Any?) {
        if (log(LogLevel.Warning)) {
            Log.w(message.tagged, *args)
        }
    }
    fun w(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (log(LogLevel.Warning)) {
            Log.w(t, message.tagged, *args)
        }
    }

    fun i(message: String, vararg args: Any?) {
        if (log(LogLevel.Info)) {
            Log.i(message.tagged, *args)
        }
    }
    fun i(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (log(LogLevel.Info)) {
            Log.i(t, message.tagged, *args)
        }
    }

    fun d(message: String, vararg args: Any?) {
        if (log(LogLevel.Debug)) {
            Log.d(message.tagged, *args)
        }
    }
    fun d(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (log(LogLevel.Debug)) {
            Log.d(t, message.tagged, *args)
        }
    }

    fun v(message: String, vararg args: Any?) {
        if (log(LogLevel.Verbose)) {
            Log.v(message.tagged, *args)
        }
    }
    fun v(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (log(LogLevel.Verbose)) {
            Log.v(t, message.tagged, *args)
        }
    }
}