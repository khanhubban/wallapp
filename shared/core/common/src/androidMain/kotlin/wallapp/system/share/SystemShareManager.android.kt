package wallapp.system.share

import android.app.Activity
import android.content.Intent
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.activity

class SystemShareManagerAndroid(
    private val uiControllerManager: UiControllerManager,
) : SystemShareManager {

    override val isAvailable: Boolean
        get() = true

    private val activity: Activity?
        get() = uiControllerManager.currentUiController?.activity

    override fun share(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(shareIntent, "Share via")
        activity?.startActivity(chooser)
    }
}