package wallapp.util

object MainThreadCheckerDesktop {

    fun initialize() {
        mainThread
    }

    val mainThread: Thread = Thread.currentThread()
}

actual fun isMainThread(): Boolean {
    return Thread.currentThread() == MainThreadCheckerDesktop.mainThread
}
