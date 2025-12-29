package wallapp.app

/**
 * Special case function to returns an instance of [AppViewModel].
 * Note that there is a single [AppViewModel] instance per application, so subsequent calls to this
 * function will return the same instance.
 */
expect fun resolveAppViewModel(): AppViewModel