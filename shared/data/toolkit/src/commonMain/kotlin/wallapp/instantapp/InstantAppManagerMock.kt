package wallapp.instantapp

import wallapp.system.ui.controller.UiController


class InstantAppManagerMock(
    override var isInstantApp: Boolean = false,
) : InstantAppManager {

    override fun showInstallPrompt(uiController: UiController, requestCode: Int): Boolean = false
}