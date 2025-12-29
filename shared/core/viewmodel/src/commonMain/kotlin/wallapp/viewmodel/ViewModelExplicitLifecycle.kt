package wallapp.viewmodel

interface ViewModelExplicitLifecycle {

    /**
     * Implement this method to clean up resources when the ViewModel is no longer used and
     * will be destroyed. This implementation may by as simple as calling onCleared().
     */
    fun destroy()
}