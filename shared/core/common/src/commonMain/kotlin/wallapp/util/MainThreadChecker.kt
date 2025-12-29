package wallapp.util

/** Returns true if the calling thread is the main thread.  */
expect fun isMainThread(): Boolean

/** Asserts if called from the main thread. */
fun assertNotMainThread(onMainThread: (() -> Unit)? = null) {
    if (isMainThread()) {
        onMainThread?.invoke()
        throw IllegalStateException("Cannot perform operation on the main thread.")
    }
}

/**
 * Helper class which asserts if if an operation is executed on the main thread.
 */
class MainThreadChecker(private var allowMainThreadOperations: Boolean) {

    fun assertNotMainThread() {
        if (allowMainThreadOperations) {
            return
        }
        wallapp.util.assertNotMainThread()
    }
}
