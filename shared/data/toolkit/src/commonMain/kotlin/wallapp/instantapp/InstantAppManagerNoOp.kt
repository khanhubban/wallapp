package wallapp.instantapp

import wallapp.system.ui.controller.UiController


class InstantAppManagerNoOp : InstantAppManager {

    override val isInstantApp: Boolean
        get() = false

    override fun showInstallPrompt(uiController: UiController, requestCode: Int): Boolean = false
}