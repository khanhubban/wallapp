package wallapp.log

object LogEmitterNoOp : LogEmitter {
    override fun e(message: String, vararg args: Any?) { }

    override fun e(t: Throwable?, message: String?, vararg args: Any?) { }

    override fun w(message: String, vararg args: Any?) { }

    override fun w(t: Throwable?, message: String?, vararg args: Any?) { }

    override fun i(message: String, vararg args: Any?) { }

    override fun i(t: Throwable?, message: String?, vararg args: Any?) { }

    override fun d(message: String, vararg args: Any?) { }

    override fun d(t: Throwable?, message: String?, vararg args: Any?) { }

    override fun v(message: String, vararg args: Any?) { }

    override fun v(t: Throwable?, message: String?, vararg args: Any?) { }
}