package wallapp.pixel.alert

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIViewController
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.currentUiViewController

class AlertManagerIos(
    private val uiControllerManager: UiControllerManager,
    private val coroutineScopeMain: CoroutineScope,
) : AlertManager {

    private val uiViewController: UIViewController
        get() = uiControllerManager.currentUiViewController
            ?: throw IllegalStateException("No current UI view controller")

    override val currentDialog: MutableStateFlow<AlertViewState?> = MutableStateFlow(null)

    override val showingDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun show(alertViewState: AlertViewState) {
        val title = alertViewState.title.string
        val message = alertViewState.message?.string
        val primaryButtonText = alertViewState.buttonPrimary.string
        val primaryButtonOnClick = alertViewState.buttonPrimaryOnClick
        val secondaryButtonText = alertViewState.buttonSecondary?.string
        val secondaryButtonOnClick = alertViewState.buttonSecondaryOnClick

        val alertController = UIAlertController.alertControllerWithTitle(
            title,
            message,
            UIAlertControllerStyleAlert,
        )

        if (secondaryButtonText != null) {
            val button2Action = UIAlertAction.actionWithTitle(
                secondaryButtonText,
                UIAlertActionStyleDefault,
            ) { action ->
                secondaryButtonOnClick?.invoke()
                onDismiss()
            }
            alertController.addAction(button2Action)
        }

        val button1Action = UIAlertAction.actionWithTitle(
            primaryButtonText,
            UIAlertActionStyleDefault
        ) { action: UIAlertAction? ->
            primaryButtonOnClick?.invoke()
            onDismiss()
        }
        alertController.addAction(button1Action)

        // Explicitly set the dialog to be shown on the main thread
        coroutineScopeMain.launch {
            withContext(Dispatchers.Main) {
                uiViewController.presentModalViewController(alertController, true)
                currentDialog.value = alertViewState
                showingDialog.value = true
            }
        }
    }

    override fun dismissCurrentDialog() {
        coroutineScopeMain.launch {
            withContext(Dispatchers.Main) {
                uiViewController.dismissModalViewControllerAnimated(true)
                onDismiss()
            }
        }
    }

    private fun onDismiss() {
        showingDialog.value = false
        currentDialog.value = null
    }
}