package wallapp.log

interface LogEmitter {
    fun e(message: String, vararg args: Any?)
    fun e(t: Throwable?, message: String? = null, vararg args: Any?)
    fun w(message: String, vararg args: Any?)
    fun w(t: Throwable?, message: String? = null, vararg args: Any?)
    fun i(message: String, vararg args: Any?)
    fun i(t: Throwable?, message: String? = null, vararg args: Any?)
    fun d(message: String, vararg args: Any?)
    fun d(t: Throwable?, message: String? = null, vararg args: Any?)
    fun v(message: String, vararg args: Any?)
    fun v(t: Throwable?, message: String? = null, vararg args: Any?)
}