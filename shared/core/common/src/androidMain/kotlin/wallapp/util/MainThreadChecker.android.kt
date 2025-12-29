package wallapp.util

import android.os.Looper


/** Returns true if the calling thread is the main thread.  */
actual fun isMainThread(): Boolean {
    return Looper.getMainLooper().thread === Thread.currentThread()
}