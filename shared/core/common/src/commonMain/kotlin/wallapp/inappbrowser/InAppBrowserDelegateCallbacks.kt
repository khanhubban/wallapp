package wallapp.inappbrowser

interface InAppBrowserDelegateCallbacks {

    fun onShow()

    fun onDismiss()

    fun onOpenToSystemBrowser()

    fun onError()
}