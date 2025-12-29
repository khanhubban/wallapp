package wallapp.system.ui.controller

object UiControllerManagerNoOp : UiControllerManager {

    override val currentUiController: UiController
        get() = UiControllerNoOp
}
