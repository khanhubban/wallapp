package wallapp.util

import platform.Foundation.NSThread

/** Returns true if the calling thread is the main thread.  */
actual fun isMainThread(): Boolean {
    return NSThread.isMainThread()
}
