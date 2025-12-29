package wallapp.apprestarter

import android.content.Context
import com.jakewharton.processphoenix.ProcessPhoenix
import wallapp.system.toast.ToastDisplayController
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.currentActivity

class AppRestarterDefault(
    private val uiControllerManager: UiControllerManager,
    private val toastDisplayController: ToastDisplayController,
) : AppRestarter {

    override val enabled: Boolean
        get() = true

    private fun isRestartProcess(context: Context): Boolean {
        return ProcessPhoenix.isPhoenixProcess(context)
    }

    override fun restartApp() {
        val currentActivity = uiControllerManager.currentActivity ?: return
        toastDisplayController.showToast("Restarting app…")
        ProcessPhoenix.triggerRebirth(currentActivity)
    }
}