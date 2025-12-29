package wallapp.inappbrowser

interface InAppBrowserDelegate {

    fun show(url: String, callbacks: InAppBrowserDelegateCallbacks)
}