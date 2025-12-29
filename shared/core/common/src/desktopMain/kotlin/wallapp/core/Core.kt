package wallapp.core

import wallapp.util.MainThreadCheckerDesktop

actual fun coreInit() {
    MainThreadCheckerDesktop.initialize()
}