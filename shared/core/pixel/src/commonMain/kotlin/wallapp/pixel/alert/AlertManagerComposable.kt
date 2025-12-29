package wallapp.pixel.alert

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class AlertManagerComposable : AlertManager {

    override val currentDialog: MutableStateFlow<AlertViewState?> = MutableStateFlow(null)

    override fun show(alertViewState: AlertViewState) {
        currentDialog.value = alertViewState
    }

    fun dismiss() {
        currentDialog.value = null
    }

    override val showingDialog: Flow<Boolean> = currentDialog.map { it != null }

    override fun dismissCurrentDialog() {
        dismiss()
    }
}
