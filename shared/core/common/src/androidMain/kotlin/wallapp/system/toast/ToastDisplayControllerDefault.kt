package wallapp.system.toast

import android.content.Context
import android.widget.Toast
import wallapp.context.toast
import wallapp.log.Log

class ToastDisplayControllerDefault(
    private val context: Context,
) : ToastDisplayController {

    private var lastToast: Toast? = null

    override fun showToast(message: String, isLong: Boolean, cancelPrevious: Boolean) {
        if (cancelPrevious) lastToast?.cancel()
        lastToast = context.toast(message, isLong)
        Log.d("showToast(): $message")
    }

    override fun showToast(message: Int, isLong: Boolean, cancelPrevious: Boolean) {
        return showToast(context.getString(message), isLong, cancelPrevious)
    }
}