package wallapp.pixel.alert

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

object AlertManagerNoOp : AlertManager {

    override fun show(alertViewState: AlertViewState) { }

    override val showingDialog: Flow<Boolean> = flowOf(false)
    override val currentDialog: StateFlow<AlertViewState?> = MutableStateFlow(null)

    override fun dismissCurrentDialog() { }
}