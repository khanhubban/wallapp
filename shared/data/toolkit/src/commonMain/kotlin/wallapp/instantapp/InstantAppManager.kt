package wallapp.instantapp

import wallapp.system.ui.controller.UiController


interface InstantAppManager {

    val isInstantApp: Boolean

    /**
     * [requestCode] is passed to startActivityForResult().
     */
    fun showInstallPrompt(uiController: UiController, requestCode: Int): Boolean
}
