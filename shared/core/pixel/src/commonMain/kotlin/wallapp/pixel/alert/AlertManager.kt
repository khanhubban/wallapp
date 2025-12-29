package wallapp.pixel.alert

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Display an alert dialog to the user. iOS uses the system Alert, other platforms render via
 * Compose.
 *
 * Note: this is a simple API, with no ability to update the UI dynamically.
 *
 * Dismissing the dialog is handled by the user clicking a button, or in Compose, tapping the scrim.
 */
interface AlertManager {

    fun show(alertViewState: AlertViewState)

    val showingDialog: Flow<Boolean>
    val currentDialog: StateFlow<AlertViewState?>

    fun dismissCurrentDialog()
}