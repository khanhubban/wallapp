package wallapp.system.dispatch

import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

object Dispatch {

    fun dispatchOnMain(block: () -> Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            block()
        }
    }
}