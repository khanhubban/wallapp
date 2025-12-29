package wallapp.log


class LogEmitterIos : LogEmitter {

    private fun validateMessage(message: String?, vararg args: Any?) {
        if (message == null) return
        if (args.isNotEmpty()) {
            message.count { it == '%' }.let { count ->
                if (count > 0) {
                    require(count == args.size) {
                        "Message contains $count placeholders, but ${args.size} arguments were provided.\n  message: $message, args: ${args.joinToString()}"
                    }
                }
            }
        }
    }

    private fun printInternal(type: String, throwable: Throwable?, message: String?, vararg args: Any?) {
        val formattedMessage = formatLogMessage(message, *args)
//        NSLog("[$type]: $formattedMessage\n")
        // Some strings such as ImageHashes crash under NSLog. Kotlin's print() is more reliable ()
        print("[$type]: $formattedMessage\n")
    }

    override fun e(message: String, vararg args: Any?) {
//        Timber.tag(tag)
//        validateMessage(message, *args)
//        Timber.e(message, *args)
        printInternal("E", throwable = null, message, args, )
    }

    override fun e(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("E", t, message, args)
    }

    override fun w(message: String, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("W", throwable = null, message, args)
    }

    override fun w(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("W", t, message, args)
    }

    override fun i(message: String, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("I", throwable = null, message, args)
    }

    override fun i(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("I", t, message, args)
    }

    override fun d(message: String, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("D", throwable = null, message, args)
    }

    override fun d(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("D", t, message, args)
    }

    override fun v(message: String, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("V", throwable = null, message, args)
    }

    override fun v(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        printInternal("V", t, message, args)
    }
}
