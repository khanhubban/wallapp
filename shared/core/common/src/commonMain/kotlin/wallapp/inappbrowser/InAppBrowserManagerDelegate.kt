package wallapp.inappbrowser

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class InAppBrowserManagerDelegate(
    private val delegate: InAppBrowserDelegate,
) : InAppBrowserManager {

    companion object {
        val Log = InAppBrowserManagerLogger
    }

    override val isShowing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private fun setIsShowing(value: Boolean) {
        if (isShowing.value == value) return
        Log.d("isShowing: ${isShowing.value} -> $value")
        isShowing.value = value
    }

    override fun show(url: String): Flow<InAppBrowserEvent> {
        val inAppBrowserEvents = MutableSharedFlow<InAppBrowserEvent>(replay = 0)
        setIsShowing(false)

        val callbacks = object : InAppBrowserDelegateCallbacks {
            override fun onShow() {
                Log.d("onShow")
                setIsShowing(true)
                inAppBrowserEvents.tryEmit(InAppBrowserEvent.Shown)
            }

            override fun onDismiss() {
                Log.d("onDismiss")
                setIsShowing(false)
                inAppBrowserEvents.tryEmit(InAppBrowserEvent.Dismissed)
            }

            override fun onOpenToSystemBrowser() {
                Log.d("onOpenToSystemBrowser")
                setIsShowing(false)
                inAppBrowserEvents.tryEmit(InAppBrowserEvent.OpenedToSystemBrowser)
            }

            override fun onError() {
                Log.d("onError")
                setIsShowing(false)
                inAppBrowserEvents.tryEmit(InAppBrowserEvent.Error)
            }
        }

        delegate.show(url, callbacks)

        return inAppBrowserEvents
    }
}