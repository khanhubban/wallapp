package wallapp.inappupdate

import kotlinx.coroutines.flow.StateFlow


interface InAppUpdateChecker {
    val appUpdateRequired: StateFlow<Boolean>
}