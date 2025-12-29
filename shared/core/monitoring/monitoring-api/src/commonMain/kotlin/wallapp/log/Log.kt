package wallapp.log

import wallapp.log.Log.emitter
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("LogKt")
object Log {
    private var disabled: Boolean = true
    /**
     * Set whether logging is disabled. This is useful for disabling logging in production builds.
     * Note: even if this is false, logging will still only occur according to the current [emitter].
     */
    fun setDisabled(disabled: Boolean) {
        this.disabled = disabled
        emitter?.i("Log.setDisabled(disabled=$disabled)")
    }

    private var emitter: LogEmitter? = null
    fun registerEmitter(emitter: LogEmitter) {
        this.emitter = emitter
    }

    fun e(message: String) {
        if (disabled) return
        emitter?.e(message)
    }

    fun e(message: String, vararg args: Any?) {
        if (disabled) return
        emitter?.e(message, *args)
    }

    fun e(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (disabled) return
        emitter?.e(t, message, *args)
    }

    fun w(message: String) {
        if (disabled) return
        emitter?.w(message)
    }

    fun w(message: String, vararg args: Any?) {
        if (disabled) return
        emitter?.w(message, *args)
    }

    fun w(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (disabled) return
        emitter?.w(t, message, *args)
    }

    fun i(message: String) {
        if (disabled) return
        emitter?.i(message)
    }

    fun i(message: String, vararg args: Any?) {
        if (disabled) return
        emitter?.i(message, *args)
    }

    fun i(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (disabled) return
        emitter?.i(t, message, *args)
    }

    fun d(message: String) {
        if (disabled) return
        emitter?.d(message)
    }

    fun d(message: String, vararg args: Any?) {
        if (disabled) return
        emitter?.d(message, *args)
    }

    fun d(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (disabled) return
        emitter?.d(t, message, *args)
    }

    fun v(message: String) {
        if (disabled) return
        emitter?.v(message)
    }
    
    fun v(message: String, vararg args: Any?) {
        if (disabled) return
        emitter?.v(message, *args)
    }

    fun v(t: Throwable?, message: String? = null, vararg args: Any?) {
        if (disabled) return
        emitter?.v(t, message, *args)
    }
}