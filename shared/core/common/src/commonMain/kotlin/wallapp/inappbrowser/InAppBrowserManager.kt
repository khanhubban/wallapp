package wallapp.inappbrowser

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface InAppBrowserManager {

    /**
     * Will be true if the in-app browser is currently showing.
     *
     * Note: will return false if the in-app browser failed to display and the system browser was
     * opened instead.
     */
    val isShowing: StateFlow<Boolean>

    fun show(url: String): Flow<InAppBrowserEvent>
}
