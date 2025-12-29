package wallapp.system.share

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIViewController
import wallapp.system.dispatch.Dispatch.dispatchOnMain
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.uiViewController

class SystemShareManagerIos(
    private val uiControllerManager: UiControllerManager,
) : SystemShareManager {

    override val isAvailable: Boolean
        get() = true

    val uiViewController: UIViewController?
        get() = uiControllerManager.currentUiController?.uiViewController

    override fun share(text: String) {
        val textToShare = arrayListOf(text)
        val activityViewController = UIActivityViewController(activityItems = textToShare, applicationActivities = null)

        uiViewController?.also { viewController ->
            // Ensure UI updates are done on the main thread
            dispatchOnMain {
                viewController.presentViewController(activityViewController, true, null)
            }
        }
    }
}