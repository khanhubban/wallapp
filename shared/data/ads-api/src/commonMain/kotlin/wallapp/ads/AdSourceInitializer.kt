package wallapp.ads

interface AdSourceInitializer {

    enum class State {
        Unsupported,
        Uninitialized,
        Initializing,
        Initialized,
    }

    val state: State

    fun init()

}