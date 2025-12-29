package wallapp.system.ui.controller

import kotlinx.cinterop.BetaInteropApi
import platform.UIKit.UIViewController


val UiControllerManager.currentUiViewController: UIViewController?
    get() {
        val wrapper = currentUiController ?: return null
        require(wrapper is UiControllerIos)
        return wrapper.uiViewController
    }

val UiController.uiViewController: UIViewController
    get() {
        require(this is UiControllerIos)
        return uiViewController
    }

val UIViewController.uiController: UiControllerIos
    get() = UiControllerIos(this)

@OptIn(BetaInteropApi::class)
val UIViewController.className: String
    get() = this.`class`().toString()