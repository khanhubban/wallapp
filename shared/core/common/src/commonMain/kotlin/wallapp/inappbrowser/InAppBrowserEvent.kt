package wallapp.inappbrowser

sealed class InAppBrowserEvent {

    data object Shown : InAppBrowserEvent()

    data object Dismissed : InAppBrowserEvent()

    data object OpenedToSystemBrowser : InAppBrowserEvent()

    data object Error : InAppBrowserEvent()
}