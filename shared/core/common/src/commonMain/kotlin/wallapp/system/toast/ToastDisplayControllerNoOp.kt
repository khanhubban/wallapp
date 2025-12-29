package wallapp.system.toast

import wallapp.log.Log

object ToastDisplayControllerNoOp : ToastDisplayController {

    override fun showToast(message: String, isLong: Boolean, cancelPrevious: Boolean) {
        Log.i(message = "ToastDisplayControllerNoOp: $message")
    }

    override fun showToast(message: Int, isLong: Boolean, cancelPrevious: Boolean) {
        Log.i(message = "ToastDisplayControllerNoOp: $message")
    }
}